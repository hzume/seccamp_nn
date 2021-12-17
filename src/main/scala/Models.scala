import chisel3._
import romdatatype._
import modules._
import math.{log, ceil}

class MLP(val rom:MLPData, val config:Config, val in_dim:Int=784, val out_dim:Int=10) extends Module {
    val io = IO(new Bundle{
        val in = Input(Vec(in_dim, SInt(rom.model_arch.in_w.W)))
        val out = Output(Vec(out_dim, SInt(10.W)))
    })
    val fc1 = 
    if (config.parallelize) {
        Module(new Linear_p(in_dim, rom.model_arch.hidden_dim, rom.fc(0), rom.model_arch.in_w))
    }        
    else { 
        Module(new Linear(in_dim, rom.model_arch.hidden_dim, rom.fc(0), rom.model_arch.in_w, 14))
    }
    val out_w1 = ceil(log(in_dim) / log(2)).asInstanceOf[Int] + rom.model_arch.in_w

    val bn1 = Module(new SBN(rom.model_arch.hidden_dim, rom.bn(0), out_w1))
    val bi1 = Module(new Binarize(rom.model_arch.hidden_dim, out_w1))
    val bn_bi1 = Module(new BN_BI(rom.model_arch.hidden_dim, rom.bn(0), out_w1))
    
    val fc2 =
    if (config.parallelize) {
        Module(new Linear_p(rom.model_arch.hidden_dim, rom.model_arch.hidden_dim, rom.fc(1), 2))
    }
    else {
        Module(new Linear(rom.model_arch.hidden_dim, rom.model_arch.hidden_dim, rom.fc(1), 2, 8))
    }
    val out_w2 = ceil(log(rom.model_arch.hidden_dim) / log(2)).asInstanceOf[Int] + 2

    val bn2 = Module(new SBN(rom.model_arch.hidden_dim, rom.bn(1), out_w2))
    val bi2 = Module(new Binarize(rom.model_arch.hidden_dim, out_w2))
    val bn_bi2 = Module(new BN_BI(rom.model_arch.hidden_dim, rom.bn(1), out_w2))
    
    val fc3 =
    if (config.parallelize) {
        Module(new Linear_p(rom.model_arch.hidden_dim, out_dim, rom.fc(2), 2))
    }
    else {
        Module(new Linear(rom.model_arch.hidden_dim, out_dim, rom.fc(2), 2, 8))
    }
    val out_w3 = ceil(log(rom.model_arch.hidden_dim) / log(2)).asInstanceOf[Int] + 2
    val bn3 = Module(new SBN(out_dim, rom.bn(2), out_w3))

    if (rom.model_arch.precalculate_bn) {
        fc1.io.in := io.in
        bn_bi1.io.in := fc1.io.out
        fc2.io.in := bn_bi1.io.out
        bn_bi2.io.in := fc2.io.out
        fc3.io.in := bn_bi2.io.out
        bn3.io.in := fc3.io.out
        io.out := bn3.io.out
    }
    else {
        fc1.io.in := io.in
        bn1.io.in := fc1.io.out
        bi1.io.in := bn1.io.out
        fc2.io.in := bi1.io.out
        bn2.io.in := fc2.io.out
        bi2.io.in := bn2.io.out
        fc3.io.in := bi2.io.out
        bn3.io.in := fc3.io.out
        io.out := bn3.io.out
    }
}