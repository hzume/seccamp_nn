import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
from torch.optim.lr_scheduler import ExponentialLR
from torch.utils.data import DataLoader
import torch.utils.data
import torchvision
import torchvision.transforms as transforms

import matplotlib.pyplot as plt
import numpy as np
import argparse
import json
from logging import StreamHandler, FileHandler, Formatter, getLogger, DEBUG, INFO
import datetime
import os
import shutil
import modules
import models

def main():
    global logger

    args = get_args()
    torch.manual_seed(args.seed)
    device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")

    log_dir, run_id = get_log_dir()
    logger = setting_logger(log_dir)
    logger.info(f"Run ID: {run_id}")
    logger.info("comment: " + args.comment)
    logger.info(f"Using {device}")
    logger.info("")
    logger.info("Hyper Parameters:")
    args_dict = vars(args)
    for k, v in args_dict.items():
        logger.info(k + f": {v}")

    try:
        model = models.TNN_MLP(28*28, 10, args.hidden_dim, args.hidden_layers, args.mu, args.precalculate_bn)
        model = model.to(device)

        train_loader, val_loader, test_loader, q_test_loader = get_dataloader(args)
        train_and_evaluate(args, model, train_loader, val_loader, log_dir, device)

        test(args, model, test_loader, device)
        set_state_for_qtest(args, model)
        q_test(args, model, q_test_loader, device)
        param_dict = quantize_param(args, model)
        param_dict = gen_test_data(args, model, q_test_loader, param_dict, device)
        save_param(args, param_dict, run_id)
    except:
        import traceback
        traceback.print_exc()
        shutil.rmtree(log_dir)
    
def get_log_dir(base_log_dir="train_scripts/log/"):
    date = datetime.datetime.now().strftime('%Y_%m_%d_')
    idx = 1
    while True:
        run_id = date + str(idx)
        log_dir = base_log_dir + run_id
        if not os.path.exists(log_dir):
            break
        idx += 1
    os.mkdir(log_dir)
    return log_dir, run_id

def setting_logger(log_dir):
    logger = getLogger(__name__)
    logger.setLevel(DEBUG)
    formatter = Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    h = FileHandler(os.path.join(log_dir, "train.log"))
    h.setLevel(INFO)
    h.setFormatter(formatter)
    sh = StreamHandler()
    sh.setLevel(DEBUG)
    sh.setFormatter(formatter)
    logger.addHandler(h)
    logger.addHandler(sh)
    return logger

def get_args(config_path = "train_scripts/config/config.json"):
    with open(config_path, "r") as f:
        data = f.read()
    config = json.loads(data)
    parser = argparse.ArgumentParser()
    parser.add_argument("--comment", type=str)
    parser.add_argument("--hidden_dim", default=config["hidden_dim"], type=int)
    parser.add_argument("--hidden_layers", default=config["hidden_layers"], type=int)
    parser.add_argument("--precalculate_bn", default=config["precalculate_bn"], type=bool)
    parser.add_argument("--mu", default=config["mu"])
    parser.add_argument("--train_rate", default=config["train_rate"])
    parser.add_argument("--input_bit", default=config["input_bit"], type=int)
    parser.add_argument("--max_epoch", default=config["max_epoch"], type=int)
    parser.add_argument("--batch_size", default=config["batch_size"],  type=int)
    parser.add_argument("--lr", default=config["lr"])
    parser.add_argument("--seed", default=config["seed"], type=int)
    args = parser.parse_args()
    return args

def get_dataloader(args):
    def to1dim(x):
        return torch.flatten(x, 0, -1)

    def quantize(x):
        return torch.round(x*(2**(args.input_bit-1)-1))

    transform = transforms.Compose([
        transforms.ToTensor(),
        transforms.Normalize((0.1307,), (1,)),
        transforms.Lambda(to1dim),
    ])

    q_transform = transforms.Compose([
        transforms.ToTensor(),
        transforms.Normalize((0.1307,), (1,)),
        transforms.Lambda(to1dim),
        transforms.Lambda(quantize),
    ])

    trainval_data = torchvision.datasets.MNIST(root="./", train=True, download=True, transform=transform)
    n_samples = len(trainval_data)
    train_size = int(n_samples * args.train_rate)
    val_size = n_samples - train_size
    train_data, val_data = torch.utils.data.random_split(trainval_data, [train_size, val_size])
    train_loader = DataLoader(train_data, batch_size=args.batch_size, shuffle=True)
    val_loader = DataLoader(val_data, batch_size=args.batch_size, shuffle=True)
    test_data = torchvision.datasets.MNIST(root="./", train=False, download=True, transform=transform)
    test_loader = DataLoader(test_data, batch_size=1, shuffle=True)
    q_test_data = torchvision.datasets.MNIST(root="./", train=False, download=True, transform=q_transform)
    q_test_loader = DataLoader(q_test_data, batch_size=1, shuffle=True)
    return train_loader, val_loader, test_loader, q_test_loader

def train_and_evaluate(args, model, train_loader, val_loader, log_dir, device):
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.Adam(params=model.parameters(), lr=args.lr)

    train_size = len(train_loader.dataset)
    val_size = len(val_loader.dataset)
    epochs = np.arange(1, args.max_epoch+1)
    train_losses = []
    val_losses = []
    model.train()
    logger.info("")
    logger.info("Start Training")
    for epoch in range(args.max_epoch):
        if epoch == 19:
            for param_group in optimizer.param_groups:
                param_group["lr"] /= 10
        if epoch == 39:
            for param_group in optimizer.param_groups:
                param_group["lr"] /= 10
        train_loss = 0
        for i, (x, t) in enumerate(train_loader):
            x = x.to(device)
            t = t.to(device)
            optimizer.zero_grad()
            y = model(x)
            loss = criterion(y, t)
            loss.backward()
            optimizer.step()
            train_loss += loss.sum().item()

        train_loss /= train_size
        train_losses.append(train_loss) 

        model.eval()
        val_loss = 0
        acc = 0
        with torch.no_grad():
            for i, (x, t) in enumerate(val_loader):
                x = x.to(device)
                t = t.to(device)
                y = model(x)
                predicted = torch.argmax(y.data, 1)
                acc += (predicted == t).sum().item()
                loss = criterion(y, t)
                val_loss += loss.sum().item()
                
            acc /= val_size
            acc *= 100
            val_loss /= val_size
            val_losses.append(val_loss)
        model.train()
        logger.info(f"epoch:{epoch+1}/{args.max_epoch}\t train_loss:{train_loss:.6f}\t val_loss:{val_loss:.6f}\t val_acc:{acc:.2f}")

    logger.info("Finish")

    torch.save(model.state_dict(), os.path.join(log_dir, "model.pth"))
    logger.info("Saved Model parameters")

    fig, ax = plt.subplots()
    ax.plot(epochs, train_losses, label="train_loss")
    ax.plot(epochs, val_losses, label="valid_loss")
    ax.legend()
    fig.savefig(os.path.join(log_dir, "loss.png"))

def set_state_for_qtest(args, model):
    state_dict = model.state_dict()
    state_dict["bn_f.bias"] = state_dict["bn_f.bias"]*(2**(args.input_bit-1)-1)
    state_dict["bn_f.running_mean"] = state_dict["bn_f.running_mean"]*(2**(args.input_bit-1)-1)
    state_dict["bn_l.bias"] = torch.round(state_dict["bn_l.bias"])
    state_dict["bn_l.running_mean"] = torch.round(state_dict["bn_l.running_mean"])
    model.load_state_dict(state_dict)

def quantize_param(args, model):
    state_dict = model.state_dict()

    param_dict = {}
    param_dict["bn"] = []
    param_dict["fc"] = []

    param_dict["fc"].append(modules.ternarize(state_dict["fc_f.weight"], args.mu))
    bn = {}
    bn["weight"] = modules.AP2(state_dict["bn_f.weight"])
    bn["bias"] = torch.round(state_dict["bn_f.bias"])
    bn["mean"] = torch.round(state_dict["bn_f.running_mean"])
    bn["norm"] = modules.AP2(1 / torch.sqrt(state_dict["bn_f.running_var"] + 1e-5))
    bn["threshold"] = model.bn_f.precalculate_threshold()
    bn["gamma"] = modules.AP2(state_dict["bn_f.weight"] / torch.sqrt(state_dict["bn_f.running_var"] + 1e-5))
    param_dict["bn"].append(bn)

    for i in range(2, args.hidden_layers+2):
        param_dict["fc"].append(modules.ternarize(state_dict[f"hidden_fc.fc_{i-1}.weight"], args.mu))
        bn = {}
        bn["weight"] = modules.AP2(state_dict[f"hidden_bn.bn_{i-1}.weight"])
        bn["bias"] = torch.round(state_dict[f"hidden_bn.bn_{i-1}.bias"])
        bn["mean"] = torch.round(state_dict[f"hidden_bn.bn_{i-1}.running_mean"])
        bn["norm"] = modules.AP2(1 / torch.sqrt(state_dict[f"hidden_bn.bn_{i-1}.running_var"] + 1e-5))
        bn["threshold"] = model.hidden_bn[f"bn_{i-1}"].precalculate_threshold()
        bn["gamma"] = modules.AP2(state_dict[f"hidden_bn.bn_{i-1}.weight"] / torch.sqrt(state_dict[f"hidden_bn.bn_{i-1}.running_var"] + 1e-5))
        param_dict["bn"].append(bn)

    param_dict["fc"].append(modules.ternarize(state_dict["fc_l.weight"], args.mu))
    bn = {}
    bn["weight"] = modules.AP2(state_dict["bn_l.weight"])
    bn["bias"] = state_dict["bn_l.bias"]
    bn["mean"] = state_dict["bn_l.running_mean"]
    bn["norm"] = modules.AP2(1 / torch.sqrt(state_dict["bn_l.running_var"] + 1e-5))
    bn["threshold"] = model.bn_l.precalculate_threshold()
    bn["gamma"] = modules.AP2(state_dict["bn_l.weight"] / torch.sqrt(state_dict["bn_l.running_var"] + 1e-5))
    param_dict["bn"].append(bn)

    return param_dict

def test(args, model, test_loader, device):
    test_size = len(test_loader.dataset)
    model.eval()
    with torch.no_grad():
        acc = 0
        for i, (x, t) in enumerate(test_loader):
            x = x.to(device)
            t = t.to(device)
            y = model(x)
            predicted = torch.argmax(y.data, 1)
            acc += (predicted == t).sum().item()
        logger.info(f"Normal Accuracy:{100*acc/test_size}%")

def q_test(args, model, q_test_loader, device):
    test_size = len(q_test_loader.dataset)
    model.eval()

    with torch.no_grad():
        acc = 0
        for i, (x, t) in enumerate(q_test_loader):
            x = x.to(device)
            t = t.to(device)
            y = model(x)
            predicted = torch.argmax(y.data, 1)
            acc += (predicted == t).sum().item()
        logger.info(f"Quantize Accuracy:{100*acc/test_size}%")  

def get_model_arch(args, param_dict):
    model_arch = {}
    model_arch["hidden_dim"] = args.hidden_dim
    model_arch["hidden_layers"] = args.hidden_layers
    model_arch["precalculate_bn"] = args.precalculate_bn
    model_arch["in_w"] = args.input_bit
    param_dict["model_arch"] = model_arch
    return param_dict

def gen_test_data(args, model, q_test_loader, param_dict, device):
    test_size = len(q_test_loader.dataset)
    id = torch.randint(low=0, high=test_size, size=(1,))[0]
    test_dict = {}
    for i, (x, t) in enumerate(q_test_loader):
        if i == id:
            x = x.to(device)
            t = t.to(device)

            test_dict["input"] = x[0]
            test_dict["result"] = model(x)[0]

            test_dict["fc_in"] = x[0]
            test_dict["fc_out"] = model.fc_f(x)[0]
            test_dict["bn_bi_in"] = model.fc_f(x)[0]
            test_dict["bn_bi_out"] = torch.sign(model.fc_f(x) - model.bn_f.precalculate_threshold())[0]
            test_dict["bn_in"] = model.test(x)[0]
            test_dict["bn_out"] = model(x)[0]
    param_dict["test"] = test_dict
    return param_dict

def save_param(args, param_dict, run_id, param_dir="src/parameters"):
    with open(os.path.join(param_dir, "param_"+run_id+".json"), mode="w") as f:
        for k, v in param_dict.items():
            if type(v) is list:
                if type(v[0]) is dict:
                    for i in range(len(param_dict[k])):
                        for ki, vi in param_dict[k][i].items():
                            param_dict[k][i][ki] = vi.to(torch.int64).tolist()
                else:
                    for i in range(len(param_dict[k])):
                        param_dict[k][i] = param_dict[k][i].to(torch.int64).tolist()
            elif type(v) is dict:
                for k0, v0 in v.items():
                    param_dict[k][k0] = v0.to(torch.int64).tolist()
            else:    
                param_dict[k] = v.to(torch.int64).tolist()
        param_dict = get_model_arch(args, param_dict)
        f.write(json.dumps(param_dict, indent=4))

        logger.info("Rate of zero weight:")
        for i, fc in enumerate(param_dict["fc"]):
            fc = np.ravel(np.array(fc))
            logger.info(f"layer {i+1}:\t{np.count_nonzero(fc == 0)/len(fc)*100:.2f}%")

if __name__=="__main__":
    main()