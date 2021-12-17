from modules import *

class TNN_MLP(nn.Module):
    def __init__(self, in_dim, out_dim, hidden_dim, layers, mu, precalculate_bn):
        super(TNN_MLP, self).__init__()
        self.layers = layers
        self.precalculate_bn = precalculate_bn

        self.fc_f = TernaryLinear(in_dim, hidden_dim, mu, bias=False)
        self.bn_f = SBN1d(hidden_dim)

        self.hidden_fc = nn.ModuleDict({f"fc_{i+1}": TernaryLinear(hidden_dim, hidden_dim, mu, bias=False) for i in range(layers)})
        self.hidden_bn = nn.ModuleDict({f"bn_{i+1}": SBN1d(hidden_dim) for i in range(layers)})

        self.fc_l = TernaryLinear(hidden_dim, out_dim, mu, bias=False)
        self.bn_l = SBN1d(out_dim)

    def forward(self, x):
        x = self.fc_f(x)
        if self.training or (not self.precalculate_bn):
            x = self.bn_f(x)
            x = s_binarize(x)
        else:
            x = torch.sign(x - self.bn_f.precalculate_threshold())
        for i in range(self.layers):
            x = self.hidden_fc[f"fc_{i+1}"](x)
            if self.training or (not self.precalculate_bn):
                x = self.hidden_bn[f"bn_{i+1}"](x)
                x = s_binarize(x)  
            else:  
                x = torch.sign(x - self.hidden_bn[f"bn_{i+1}"].precalculate_threshold())
        x = self.fc_l(x)
        x = self.bn_l(x)
        return x

    def test(self, x):
        x = self.fc_f(x)
        if self.training or (not self.precalculate_bn):
            x = self.bn_f(x)
            x = s_binarize(x)
        else:
            x = torch.sign(x - self.bn_f.precalculate_threshold())
        for i in range(self.layers):
            x = self.hidden_fc[f"fc_{i+1}"](x)
            if self.training or (not self.precalculate_bn):
                x = self.hidden_bn[f"bn_{i+1}"](x)
                x = s_binarize(x)  
            else:  
                x = torch.sign(x - self.hidden_bn[f"bn_{i+1}"].precalculate_threshold())
        x = self.fc_l(x)
        return x

    # def forward(self, x):
    #     x = self.fc_f(x)
    #     x = self.bn_f(x)
    #     x = s_binarize(x) if self.training else binarize(x)
    #     for i in range(self.layers):
    #         x = self.hidden_fc[f"fc_{i+1}"](x)
    #         x = self.hidden_bn[f"bn_{i+1}"](x)
    #         x = s_binarize(x) if self.training else binarize(x)  
    #     x = self.fc_l(x)
    #     x = self.bn_l(x)
    #     return x

class BNN_MLP(nn.Module):
    def __init__(self, in_dim, out_dim, hidden_dim, layers):
        super(BNN_MLP, self).__init__()
        self.layers = layers

        self.fc_f = BinaryLinear(in_dim, hidden_dim, bias=False)
        self.bn_f = SBN1d(hidden_dim) 

        self.hidden_fc = nn.ModuleDict({f"fc_{i+1}": BinaryLinear(hidden_dim, hidden_dim, bias=False) for i in range(layers)})
        self.hidden_bn = nn.ModuleDict({f"bn_{i+1}": SBN1d(hidden_dim) for i in range(layers)})

        self.fc_l = BinaryLinear(hidden_dim, out_dim, bias=False)
        self.bn_l = SBN1d(out_dim)

    def forward(self, x):
        x = self.fc_f(x)
        x = self.bn_f(x)
        x = s_binarize(x) if self.training else binarize(x)
        for i in range(self.layers):
            x = self.hidden_fc[f"fc_{i+1}"](x)
            x = self.hidden_bn[f"bn_{i+1}"](x)
            x = s_binarize(x) if self.training else binarize(x)
        x = self.fc_l(x)
        x = self.bn_l(x)
        return x

class CNN_Cifar10(nn.Module):
    def __init__(self):
        super(CNN_Cifar10, self).__init__()
        self.block1 = nn.Sequential(
            BinaryConv2d(in_channels=3, out_channels=384, kernel_size=3),
            #nn.MaxPool2d(kernel_size=2, stride=2),
            SBN2d(384),
        )

        self.block2 = nn.Sequential(
            BinaryConv2d(in_channels=384, out_channels=384, kernel_size=3),
            nn.MaxPool2d(kernel_size=2, stride=2),
            SBN2d(384)
        )

        self.block3 = nn.Sequential(
            BinaryConv2d(in_channels=384, out_channels=768, kernel_size=3),
            #nn.MaxPool2d(kernel_size=2),
            SBN2d(768)
        )

        self.block4 = nn.Sequential(
            BinaryConv2d(in_channels=768, out_channels=768, kernel_size=3)
        )

        self.block5 = nn.Sequential(
            BinaryConv2d(in_channels=768, out_channels=1536, kernel_size=3),
            #nn.MaxPool2d(kernel_size=2, stride=2),
            SBN2d(1536)
        )

        self.block6 = nn.Sequential(
            BinaryConv2d(in_channels=1536, out_channels=512, kernel_size=3),
            nn.MaxPool2d(kernel_size=2, stride=2),
            SBN2d(512)
        )
        self.fc1 = TernaryLinear(512 * 3 * 3, 1024)
        self.bn1 = SBN1d(1024)
        self.fc2 = TernaryLinear(1024, 1024)
        self.bn2 = SBN1d(1024)
        self.fc3 = TernaryLinear(1024, 10)
        self.bn3 = SBN1d(10)
    
    def forward(self, x):
        x = self.block1(x)
        x = s_binarize(x) if self.training else binarize(x)
        x = self.block2(x)
        x = s_binarize(x) if self.training else binarize(x)
        x = self.block3(x)
        x = s_binarize(x) if self.training else binarize(x)
        x = self.block4(x)
        x = s_binarize(x) if self.training else binarize(x)
        x = self.block5(x)
        x = s_binarize(x) if self.training else binarize(x)
        x = self.block6(x)
        x = s_binarize(x) if self.training else binarize(x)
        x = x.view(x.size(0), -1)
        x = self.fc1(x)
        x = self.bn1(x)
        x = s_binarize(x) if self.training else binarize(x)
        x = self.fc2(x)
        x = self.bn2(x)
        x = s_binarize(x) if self.training else binarize(x)
        x = self.fc3(x)
        x = self.bn3(x)
        return x