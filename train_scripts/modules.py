import torch
import torch.nn as nn
import torch.nn.functional as F
from collections import OrderedDict
import numpy as np
import math

def AP2(x):
    ret = x.abs().log2()
    ret = ret.sign() * ret.abs().floor()
    return ret
    #return torch.round(torch.log2(torch.abs(x)))

def g(u, v):
    return v * (pow(2, (math.floor(math.log2(4*u/3/v)))) - u/v)**2 - u*u/v

def search_k(sorted_array):
    n = sorted_array.size(0)
    min_g = 1e10
    sum = 0
    k_opt = n-1
    for k in range(1, n + 1):
        sum += sorted_array[k-1].data
        g_val = g(sum, k)
        if min_g > g_val:
            min_g = g_val
            k_opt = k
    return k_opt

class StochasticBinarize(torch.autograd.Function):
    @staticmethod
    def forward(ctx, i):
        ctx.save_for_backward(i)
        j = torch.rand(size=i.size(), device=i.device) - 1/2 + i/2
        result = torch.sign(j)
        return result

    @staticmethod
    def backward(ctx, grad_output):
        input, = ctx.saved_tensors
        grad_input = grad_output.clone()
        grad_input[input > 1] = 0
        grad_input[input < -1] = 0
        return grad_input
s_binarize = StochasticBinarize.apply

class Binarize(torch.autograd.Function):
    @staticmethod
    def forward(ctx, i):
        ctx.save_for_backward(i)
        result = torch.sign(i)
        return result
    
    @staticmethod
    def backward(ctx, grad_output):
        input, = ctx.saved_tensors
        grad_input = grad_output.clone()
        grad_input[input > 1] = 0
        grad_input[input < -1] = 0
        return grad_input
binarize = Binarize.apply

class Ternarize(torch.autograd.Function):
    @staticmethod
    def forward(self, i, mu):
        self.save_for_backward(i)
        result = torch.zeros(i.size())
        result[i >= mu] = 1
        result[i < -mu] = -1
        return result

    @staticmethod
    def backward(ctx, grad_output):
        input, = ctx.saved_tensors
        grad_input = grad_output.clone()
        grad_input[input > 1] = 0
        grad_input[input < -1] = 0
        return grad_input, None
ternarize = Ternarize.apply

class SBN1d(nn.BatchNorm1d):
    def precalculate_threshold(self):
        return torch.round(self.running_mean - self.bias * torch.sqrt(self.running_var+self.eps) / self.weight)

    def forward(self, x):
        if self.training:
            return super().forward(x)
        else:
            C_x = x - torch.round(self.running_mean)
            gamma = AP2(self.weight / torch.sqrt(self.running_var + self.eps))
            x_hat = torch.empty((x.size(0), x.size(1)))
            for i in range(x.size(1)):
                if gamma[i] >= 0:
                    x_hat[:, i] = C_x[:, i].to(torch.int) << gamma[None, i].to(torch.int)
                else:
                    x_hat[:, i] = C_x[:, i].to(torch.int) >> (-gamma[None, i]).to(torch.int)
            assert torch.all((x_hat.to(int) - x_hat).abs() < 1e-5)
            return x_hat + torch.round(self.bias)

class SBN2d(nn.BatchNorm2d):
    def forward(self, x):
        if self.training:
            return super().forward(x)
        else:
            C_x = x - self.running_mean.unsqueeze(0).unsqueeze(2).unsqueeze(3)
            x_hat = C_x << AP2(1 / torch.sqrt(self.running_var + self.eps)).unsqueeze(0).unsqueeze(2).unsqueeze(3)
            return (x_hat << AP2(self._parameters["weight"]).unsqueeze(0).unsqueeze(2).unsqueeze(3)) + self._parameters["bias"].unsqueeze(0).unsqueeze(2).unsqueeze(3)

class TernaryLinear(nn.Linear):
    def __init__(self, in_features, out_features, mu, bias) -> None:
        super().__init__(in_features, out_features, bias=bias)
        self.mu = mu
    def forward(self, x):
        self.weight.data = torch.clip(self.weight.data, -1, 1)
        ternay_weight = ternarize(self.weight, self.mu)
        return F.linear(x, ternay_weight)

class BinaryLinear(nn.Linear):
    def forward(self, x):
        binary_weight = binarize(self.weight) 
        return F.linear(x, binary_weight)

class BinaryConv2d(nn.Conv2d):
    def forward(self, x):
        self.weight.data = torch.clip(self.weight.data, -1, 1)
        binary_weight = ternarize(self.weight) 
        return F.conv2d(input=x, weight=binary_weight, stride=self.stride, padding=self.padding, dilation=self.dilation, groups=self.groups)

