import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
from torch.optim.lr_scheduler import StepLR
from torch.utils.data import DataLoader
from torch.utils.data.dataset import Subset
import torchvision
import torchvision.transforms as transforms
import modules
import matplotlib.pyplot as plt
import numpy as np
import argparse

torch.manual_seed(0)

parser = argparse.ArgumentParser()
parser.add_argument("max_epoch", type=int)
parser.add_argument("batch_size", type=int)
parser.add_argument("hidden_dim", type=int)
parser.add_argument("hidden_layers", type=int)

device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")

args = parser.parse_args()
MAX_EPOCH = args.max_epoch
BATCH_SIZE = args.batch_size

def to1dim(x):
    return torch.flatten(x, 0, -1)

def ch1(x):
    return torch.unsqueeze(x, 1)

transform = transforms.Compose([
    transforms.ToTensor(),
    #transforms.Lambda(to1dim)
    transforms.Normalize((0.491, 0.482, 0.447,), (1, 1, 1,)),
])

trainval_data = torchvision.datasets.CIFAR10(root="./", train=True, download=True, transform=transform)
train_size = 10
train_idx = list(range(0, train_size))
train_data = Subset(trainval_data, train_idx)

train_loader = DataLoader(train_data, batch_size=BATCH_SIZE, shuffle=True)

test_data = torchvision.datasets.CIFAR10(root="./", train=False, download=True, transform=transform)
test_loader = DataLoader(test_data, batch_size=1, shuffle=True)

model = modules.CNN_Cifar10()
model = model.to(device)

criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(params=model.parameters(), lr=0.1)
scheduler = StepLR(optimizer=optimizer, step_size=50, gamma=0.5)

fig = plt.figure()
ax = fig.add_subplot(111)
epoch_arr = np.arange(1, MAX_EPOCH+1)
loss_arr = []
model.train()
for epoch in range(MAX_EPOCH):
    train_loss = 0
    for i, (x, t) in enumerate(train_loader):
        x = x.to(device)
        t = t.to(device)
        optimizer.zero_grad()
        y = model(x)
        loss = criterion(y, t)
        loss.backward()
        optimizer.step()
        train_loss = loss.item()
    scheduler.step()
        
    print(f"epoch:{epoch+1}/{MAX_EPOCH}\t loss:{train_loss}")
    loss_arr.append(train_loss)
ax.plot(epoch_arr, loss_arr)
plt.savefig("test.png")

# model.eval()
# with torch.no_grad():
#     acc = 0
#     total = 0
#     for i, (x, t) in enumerate(test_loader):
 
#         x = x.to(device)
#         t = t.to(device)
#         y = model(x)
#         predicted = torch.argmax(y.data, 1)
#         total += t.size(0)
#         acc += (predicted == t).sum().item()
#     print(f"Accuracy:{100*acc/total}%")

print(model.state_dict()["fc3.bias"])