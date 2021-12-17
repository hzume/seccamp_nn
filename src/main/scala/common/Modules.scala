import chisel3._
import romdatatype._
import math.{log, ceil}
import scala.collection._

package modules {


class RomReader(val in_w:Int, val addr_w:Int, val num:Int) extends Module {
    val io = IO(new Bundle {
        val data = Input(SInt(in_w.W))
        val addr = Output(UInt(addr_w.W))
        val out = Output(Vec(num, SInt(in_w.W)))
        val flag = Output(Bool())
    })

    val cnt = RegInit(0.U(addr_w.W))
    val res = RegInit(VecInit(Seq.fill(num)(0.S(in_w.W))))

    io.out := res
    io.addr := cnt
    io.flag := false.B

    when(cnt =/= num.U) {
        res(cnt) := io.data
        cnt := cnt + 1.U
    }.otherwise {
        io.flag := true.B
    }
}

class Conv(val in_size:Int, val in_c:Int, val out_c:Int, val kernel_size:Int, val stride:Int, val param:Seq[Seq[Seq[Seq[Int]]]], val in_w:Int, val out_w:Int) extends Module {
    val out_size = (in_size - kernel_size) / stride + 1 
    val io = IO(new Bundle{
        val in = Input(Vec(in_c, Vec(in_size, Vec(in_size, SInt(in_w.W)))))
        val out = Output(Vec(out_c, Vec(out_size, Vec(out_size, SInt(out_w.W)))))
    })

    val buffer = Wire(Vec(out_c, Vec(in_c, Vec(out_size, Vec(out_size, Vec(kernel_size, Vec(kernel_size, SInt(out_w.W))))))))

    for (i <- 0 until out_c) {
        for (h <- 0 until out_size; w <- 0 until out_size) {
            for (j <- 0 until in_c) {
                for (k <- 0 until kernel_size; l <- 0 until kernel_size) {
                    if (l == 0) {
                        if (k == 0) {
                            if (j == 0) {
                                buffer(i)(j)(h)(w)(k)(l) := param(i)(j)(k)(l).S * io.in(j)(h + k)(w + l)
                            }
                            else {
                                buffer(i)(j)(h)(w)(k)(l) := buffer(i)(j - 1)(h)(w)(kernel_size - 1)(kernel_size - 1) + param(i)(j)(k)(l).S * io.in(j)(h + k)(w + l)
                            }
                        } 
                        else {
                            buffer(i)(j)(h)(w)(k)(l) := buffer(i)(j)(h)(w)(k - 1)(kernel_size - 1) + param(i)(j)(k)(l).S * io.in(j)(h + k)(w + l)
                        }
                    }
                    else {
                        buffer(i)(j)(h)(w)(k)(l) := buffer(i)(j)(h)(w)(k)(l - 1) + param(i)(j)(k)(l).S * io.in(j)(h + k)(w + l)
                    }
                }
            }
            io.out(i)(h)(w) := buffer(i)(in_c - 1)(h)(w)(kernel_size - 1)(kernel_size - 1)
        }
    }
}

class MaxPool(val in_size:Int, val in_c:Int, val kernel_size:Int, val stride:Int, val in_w:Int) extends Module {
    val out_size = (in_size - kernel_size) / stride + 1
    val io = IO(new Bundle{
        val in = Input(Vec(in_c, Vec(in_size, Vec(in_size, SInt(in_w.W)))))
        val out = Output(Vec(in_c, Vec(out_size, Vec(out_size, SInt(in_w.W)))))
    })

    val buffer = Wire(Vec(in_c, Vec(out_size, Vec(out_size, Vec(kernel_size, Vec(kernel_size, SInt(in_w.W)))))))

    for (i <- 0 until in_c) {
        for (h <- 0 until out_size; w <- 0 until out_size){
            for (k <- 0 until kernel_size) {
                for (l <- 0 until kernel_size) {
                    if ((k == 0) && (l == 0)) {
                        buffer(i)(h)(w)(k)(l) := io.in(i)(stride * h + k)(stride * w + l)
                    }
                    else if (l == 0) {
                        when(io.in(i)(stride * h + k)(stride * w + l) >= buffer(i)(h)(w)(k - 1)(l)) {
                            buffer(i)(h)(w)(k)(l) := io.in(i)(stride * h + k)(stride * w + l)
                        }.otherwise {
                            buffer(i)(h)(w)(k)(l) := buffer(i)(h)(w)(k - 1)(kernel_size - 1)
                        }
                    }
                    else {
                        when(io.in(i)(stride * h + k)(stride * w + l) >= buffer(i)(h)(w)(k)(l - 1)) {
                            buffer(i)(h)(w)(k)(l) := io.in(i)(stride * h + k)(stride * w + l)
                        }.otherwise {
                            buffer(i)(h)(w)(k)(l) := buffer(i)(h)(w)(k)(l - 1)
                        }     
                    }
                }
            }
            io.out(i)(h)(w) := buffer(i)(h)(w)(kernel_size - 1)(kernel_size - 1)
        }
    }
}

class Linear_p0(val in_units:Int, val out_units:Int, val weight:Seq[Seq[Int]], val in_w:Int, val out_w:Int) extends Module {
    val io = IO(new Bundle{
        val in = Input(Vec(in_units, SInt(in_w.W)))
        val out = Output(Vec(out_units, SInt(out_w.W)))
    })

    val buffer = Wire(Vec(out_units, Vec(in_units, SInt(out_w.W))))

    for (i <- 0 until out_units) {
        for (j <- 0 until in_units if j % 2 == 0) {
            buffer(i)(j / 2) := weight(i)(j).S * io.in(j) + weight(i)(j + 1).S * io.in(j + 1)
        }
        for (j <- 0 until (in_units/2) if j % 2 == 0) {
            buffer(i)(in_units/2 + j/2) := buffer(i)(j) + buffer(i)(j + 1)
        }
        for (j <- 0 until (in_units/4) if j % 2 == 0) {
            buffer(i)(in_units/4 + in_units/2 + j/2) := buffer(i)(in_units/2 + j) + buffer(i)(in_units/2 + j + 1)
        }
        for (j <- 0 until (in_units/8) if j % 2 == 0) {
            buffer(i)(in_units/8 + in_units/4 + in_units/2 + j/2) := buffer(i)(in_units/4 + in_units/2 + j) + buffer(i)(in_units/4 + in_units/2 + j + 1)
        }
        for (j <- 0 until (in_units/16)) {
            if (j == 0) {
                buffer(i)(in_units/16 + in_units/8 + in_units/4 + in_units/2 + j) := buffer(i)(in_units/8 + in_units/4 + in_units/2 + j)
            }
            else {
                buffer(i)(in_units/16 + in_units/8 + in_units/4 + in_units/2 + j) := buffer(i)(in_units/16 + in_units/8 + in_units/4 + in_units/2 + j - 1) + buffer(i)(in_units/8 + in_units/4 + in_units/2 + j)
            }
        }
        io.out(i) := buffer(i)(in_units-1)
    }
}

class Linear_p(val in_units:Int, val out_units:Int, val weight:Seq[Seq[Int]], val in_w:Int) extends Module {
    val out_w = ceil(log(in_units) / log(2)).asInstanceOf[Int] + in_w
    val io = IO(new Bundle{
        val in = Input(Vec(in_units, SInt(in_w.W)))
        val out = Output(Vec(out_units, SInt(out_w.W)))
    })

    val buffer = Wire(Vec(out_units, Vec(in_units-1, SInt(out_w.W))))
    var nonzero_idx = mutable.Buffer.fill(out_units)(Seq[Int]())
    var num_nonzero = Seq[Int]()
    for (i <- 0 until out_units) {
        for (j <- 0 until in_units) {
            if (weight(i)(j) != 0) {
                nonzero_idx(i) = nonzero_idx(i):+ j
            }
        }
        num_nonzero = num_nonzero:+ nonzero_idx(i).length
    }

    def sum(i:Int, num:Int, res_idx:Int, idx:Int) {
        if ((num == 1) && (res_idx == -1)) {
            if (num_nonzero(i) % 2 == 0) {
                io.out(i) := buffer(i)(num_nonzero(i)-2)
                for (j <- num_nonzero(i)-1 until in_units-1) {
                    buffer(i)(j) := DontCare
                }
            }
            else {
                io.out(i) := buffer(i)(num_nonzero(i)-1)
                for (j <- num_nonzero(i) until in_units-1) {
                    buffer(i)(j) := DontCare
                }
            }
            return
        }
        if (idx == 0) {
            if (num % 2 == 0) {
                for (j <- 0 until num/2) {
                    buffer(i)(idx+j) := weight(i)(nonzero_idx(i)(2*j)).S * io.in(nonzero_idx(i)(2*j)) +
                                            weight(i)(nonzero_idx(i)(2*j+1)).S * io.in(nonzero_idx(i)(2*j+1))
                }
                sum(i, num/2, res_idx, num/2)
            }
            else {
                for (j <- 0 until (num-1)/2) {
                    buffer(i)(idx+j) := weight(i)(nonzero_idx(i)(2*j)).S * io.in(nonzero_idx(i)(2*j)) + 
                                            weight(i)(nonzero_idx(i)(2*j+1)).S * io.in(nonzero_idx(i)(2*j+1))
                }
                buffer(i)(idx+(num-1)/2) := weight(i)(nonzero_idx(i)(num-1)).S * io.in(nonzero_idx(i)(num-1)) 
                sum(i, (num-1)/2+1, res_idx, (num-1)/2+1)
            }
        }
        else {
            if (num % 2 == 0) {
                for (j <- 0 until num/2) {
                    buffer(i)(idx+j) := buffer(i)(idx-num+2*j) + buffer(i)(idx-num+2*j+1)
                }
                sum(i, num/2, res_idx, idx+num/2)
            }
            else {
                for (j <- 0 until (num-1)/2) {
                    buffer(i)(idx+j) := buffer(i)(idx-num+2*j) + buffer(i)(idx-num+2*j+1)
                }
                if (res_idx == -1) {
                    sum(i, (num-1)/2, idx-1, idx+(num-1)/2)
                }
                else {
                    buffer(i)(idx+(num-1)/2) := buffer(i)(idx-1) + buffer(i)(res_idx)
                    sum(i, (num-1)/2+1, -1, idx+(num-1)/2+1)
                }
            }
        }
    }

    for (i <- 0 until out_units) {
        sum(i, num_nonzero(i), -1, 0)
    }
}
class Linear(val in_units:Int, val out_units:Int, val weight:Seq[Seq[Int]], val in_w:Int, val out_w:Int) extends Module {
    val io = IO(new Bundle{
        val in = Input(Vec(in_units, SInt(in_w.W)))
        val out = Output(Vec(out_units, SInt(out_w.W)))
    })

    val buffer = Wire(Vec(out_units, Vec(in_units, SInt(out_w.W))))

    for (i <- 0 until out_units) {
        for (j <- 0 until in_units) {
            if (j == 0) {
                buffer(i)(j) := weight(i)(j).S * io.in(j)
            }
            else {
                buffer(i)(j) := buffer(i)(j - 1) + weight(i)(j).S * io.in(j)
            }
        }
        io.out(i) := buffer(i)(in_units - 1)
    }
}

class ShiftBatchNorm2d(val in_size:Int, val in_c:Int, val weight:Seq[Int], val bias:Seq[Int], val mean:Seq[Int], val norm:Seq[Int], val bit_w:Int) extends Module {
    val io = IO(new Bundle {
        val in = Input(Vec(in_c, Vec(in_size, Vec(in_size, SInt(bit_w.W)))))
        val out = Output(Vec(in_c, Vec(in_size, Vec(in_size, SInt(bit_w.W))))) 
    })

    val c_x = Wire(Vec(in_c, Vec(in_size, Vec(in_size, SInt(bit_w.W)))))
    val x_hat = Wire(Vec(in_c, Vec(in_size, Vec(in_size, SInt(bit_w.W)))))
    val normed_x_hat = Wire(Vec(in_c, Vec(in_size, Vec(in_size, SInt(bit_w.W)))))
    for (i <- 0 until in_c) {
        for (h <- 0 until in_size; w <- 0 until in_size) {
            c_x(i)(h)(w) := io.in(i)(h)(w) - mean(i).S(bit_w.W)
            if (norm(i) >= 0) {
                x_hat(i)(h)(w) := c_x(i)(h)(w) << norm(i).U(4.W)
            }
            else {
                x_hat(i)(h)(w) := c_x(i)(h)(w) << (-norm(i)).U(4.W)
            }
            normed_x_hat(i)(h)(w) := x_hat(i)(h)(w) << weight(i).U(4.W)
            io.out(i)(h)(w) := normed_x_hat(i)(h)(w) + bias(i).S(bit_w.W)
        }
    }
}

// class ShiftBatchNorm(val num_units:Int, val bn: BNParameter, val bit_w:Int) extends Module {
//     val io = IO(new Bundle {
//         val in = Input(Vec(num_units, SInt(bit_w.W)))
//         val out = Output(Vec(num_units, SInt(bit_w.W)))
//     })

//     val c_x = Wire(Vec(num_units, SInt(bit_w.W)))
//     val x_hat = Wire(Vec(num_units, SInt(bit_w.W)))
//     val normed_x_hat = Wire(Vec(num_units, SInt(bit_w.W)))
//     for (i <- 0 until num_units) {
//         c_x(i) := io.in(i) - bn.mean(i).S(bit_w.W)
//         if (bn.norm(i) >= 0) {
//             x_hat(i) := c_x(i) << bn.norm(i).U(4.W)    
//         }
//         else {
//             x_hat(i) := c_x(i) >> (-bn.norm(i)).U(4.W)
//         }
//         normed_x_hat(i) := x_hat(i) << bn.weight(i).U(4.W)
//         io.out(i) := normed_x_hat(i) + bn.bias(i).S(bit_w.W) 
//     }
// }

class SBN(val num_units:Int, val bn:BNParameter, val bit_w:Int) extends Module {
    val io = IO(new Bundle {
        val in = Input(Vec(num_units, SInt(bit_w.W)))
        val out = Output(Vec(num_units, SInt(bit_w.W)))
    })

    val c_x = Wire(Vec(num_units, SInt(bit_w.W)))
    val x_hat = Wire(Vec(num_units, SInt(bit_w.W)))
    for (i <- 0 until num_units) {
        c_x(i) := io.in(i) - bn.mean(i).S(bit_w.W)
        if (bn.gamma(i) >= 0) {
            x_hat(i) := c_x(i) << bn.gamma(i).U(4.W)    
        }
        else {
            x_hat(i) := c_x(i) >> (-bn.gamma(i)).U(4.W)
        }
        io.out(i) := x_hat(i) + bn.bias(i).S(bit_w.W) 
    }
}

class BN_BI(val num_units:Int, val bn:BNParameter, val bit_w:Int) extends Module {
    val io = IO(new Bundle {
        val in = Input(Vec(num_units, SInt(bit_w.W)))
        val out = Output(Vec(num_units, SInt(bit_w.W)))
    })
    val buffer = Wire(Vec(num_units, SInt(bit_w.W)))
    for (i <- 0 until num_units) {
        buffer(i) := io.in(i) - bn.threshold(i).S(bit_w.W)
        when(buffer(i) >= 0.S) {
            io.out(i) := 1.S
        }.otherwise {
            io.out(i) := -1.S
        }
    }
}

class Binarize2d(val in_c:Int, val in_size:Int, val in_w:Int) extends Module {
    val io = IO(new Bundle {
        val in = Input(Vec(in_c, Vec(in_size, Vec(in_size, SInt(in_w.W)))))
        val out = Output(Vec(in_c, Vec(in_size, Vec(in_size, SInt(2.W)))))
    })

    for (i <- 0 until in_c; j <- 0 until in_size; k <- 0 until in_size) {
        when(io.in(i)(j)(k) >= 0.S) {
            io.out(i)(j)(k) := 1.S
        }.otherwise {
            io.out(i)(j)(k) := -1.S
        }
    }
}

class Binarize(val num_units:Int, val in_w:Int) extends Module {
    val io = IO(new Bundle {
        val in = Input(Vec(num_units, SInt(in_w.W)))
        val out = Output(Vec(num_units, SInt(2.W)))
    })

    for (i <- 0 until num_units) {
        when (io.in(i) >= 0.S) {
            io.out(i) := 1.S
        }.otherwise {
            io.out(i) := -1.S
        }
    }
}



// class CNN(val in_w:Int, val out_w:Int, val cnn_param:Seq[Seq[Seq[Seq[Seq[Int]]]]], val cnn_bn:Seq[Seq[Seq[Int]]], val fc_param:Seq[Seq[Int]], val fc_bn:Seq[Seq[Seq[Int]]]) extends Module {
//     val io = IO(new Bundle{
//         val in = Input(Vec(3, Vec(32, Vec(32, SInt(in_w.W)))))
//         val out = Output(Vec(10, SInt(out_w.W)))
//     })

//     val conv1 = Module(new Conv(32, 3, 384, 3, 1, cnn_param(0), in_w, out_w))
//     val bn1 = Module(new ShiftBatchNorm2d(30, 384, cnn_bn(0)(0), cnn_bn(0)(1), cnn_bn(0)(2), cnn_bn(0)(3), out_w))
//     val bi1 = Module(new Binarize2d(384, 30, out_w))

//     val conv2 = Module(new Conv(30, 384, 384, 3, 1, cnn_param(1), 2, out_w))
//     val mp2 = Module(new MaxPool(28, 384, 2, 2, out_w))
//     val bn2 = Module(new ShiftBatchNorm2d(14, 384, cnn_bn(1)(0), cnn_bn(1)(1), cnn_bn(1)(2), cnn_bn(1)(3), out_w))
//     val bi2 = Module(new Binarize2d(384, 14, out_w))

//     val conv3 = Module(new Conv(14, 384, 768, 3, 1, cnn_param(2), 2, out_w))
//     val bn3 = Module(new ShiftBatchNorm2d(12, 768, cnn_bn(2)(0), cnn_bn(2)(1), cnn_bn(2)(2), cnn_bn(2)(3), out_w))
//     val bi3 = Module(new Binarize2d(768, 12, out_w))

//     val conv4 = Module(new Conv(12, 768, 768, 3, 1, cnn_param(3), 2, out_w))
//     val bn4 = Module(new ShiftBatchNorm2d(10, 768, cnn_bn(3)(0), cnn_bn(3)(1), cnn_bn(3)(2), cnn_bn(3)(3), out_w))
//     val bi4 = Module(new Binarize2d(1536, 10, out_w))

//     val conv5 = Module(new Conv(10, 768, 1536, 3, 1, cnn_param(4), 2, out_w))
//     val bn5 = Module(new ShiftBatchNorm2d(8, 1536, cnn_bn(4)(0), cnn_bn(4)(1), cnn_bn(4)(2), cnn_bn(4)(3), out_w))
//     val bi5 = Module(new Binarize2d(1536, 8, out_w))

//     val conv6 = Module(new Conv(8, 1536, 512, 3, 1, cnn_param(5), 2, out_w))
//     val mp6 = Module(new MaxPool(6, 512, 2, 2, out_w))
//     val bn6 = Module(new ShiftBatchNorm2d(3, 512, cnn_bn(5)(0), cnn_bn(5)(1), cnn_bn(5)(2), cnn_bn(5)(3), out_w))
//     val bi6 = Module(new Binarize2d(512, 3, out_w))

//     val fc1 = Module(new Linear_p(512*3*3, 1024, fc_param(0), 2, out_w))
//     val fc_bn1 = Module(new ShiftBatchNorm(1024, fc_bn(0)(0), fc_bn(0)(1), fc_bn(0)(2), fc_bn(0)(3), out_w))
//     val fc_bi1 = Module(new Binarize(1024, out_w))

//     val fc2 = Module(new Linear_p(1024, 1024, fc_param(1), 2, out_w))
//     val fc_bn2 = Module(new ShiftBatchNorm(1024, fc_bn(1)(0), fc_bn(1)(1), fc_bn(1)(2), fc_bn(1)(3), out_w))
//     val fc_bi2 = Module(new Binarize(1024, out_w))

//     val fc3 = Module(new Linear_p(1024, 10, fc_param(2), 2, out_w))
//     val fc_bn3 = Module(new ShiftBatchNorm(10, fc_bn(2)(0), fc_bn(2)(1), fc_bn(2)(2), fc_bn(2)(3), out_w))

//     conv1.io.in := io.in
//     bn1.io.in := conv1.io.out
//     bi1.io.in := bn1.io.out

//     conv2.io.in := bi1.io.out
//     mp2.io.in := conv2.io.out
//     bn2.io.in := mp2.io.out
//     bi2.io.in := bn2.io.out

//     conv3.io.in := bi2.io.out
//     bn3.io.in := conv3.io.out
//     bi3.io.in := bn3.io.out

//     conv4.io.in := bi3.io.out
//     bn4.io.in := conv4.io.out
//     bi4.io.in := bn4.io.out

//     conv5.io.in := bi4.io.out
//     bn5.io.in := conv5.io.out
//     bi5.io.in := bn5.io.out

//     conv6.io.in := bi5.io.out
//     mp6.io.in := conv6.io.out
//     bn6.io.in := mp6.io.out
//     bi6.io.in := bn6.io.out

//     for (i <- 0 until 512; j <- 0 until 3; k <- 0 until 3) {
//         fc1.io.in(512 * i + 3 * j + k) := bi6.io.out(i)(j)(k)
//     }
//     fc_bn1.io.in := fc1.io.out
//     fc_bi1.io.in := fc_bn1.io.out

//     fc2.io.in := fc_bi1.io.out
//     fc_bn2.io.in := fc2.io.out
//     fc_bi2.io.in := fc_bn2.io.out
    
//     fc3.io.in := fc_bi2.io.out
//     fc_bn3.io.in := fc3.io.out
    
//     io.out := fc_bn3.io.out
// }
/*
class Dense(val in_units:Int, val out_units:Int, val param:Seq[Int], val threshold:Int, val in_w:Int, val out_w:Int) extends Module {
    val io = IO(new Bundle{
        val in = Input(Vec(in_units, SInt(in_w.W)))
        val out = Output(Vec(out_units, SInt(out_w.W)))
    })

    val cnt_plus = Seq.fill(out_units)(0)
    for (i <- 0 until out_units) {
        for (j <- 0 until in_units) {
            if (param(in_units * i + j) > 0) {
                cnt_plus(i) = cnt_plus(i) + 1
            }
        }
    }

    val buffer = Wire(Vec(out_units, Vec(in_units, SInt(in_w.W))))
    val idx_plus = 0
    val idx_minus = 0
    for (i <- 0 until out_units) {
        if (2*cnt_plus(i) > out_units) {
            for (j <- 0 until in_units) {
                if (param(in_units * i + j) > 0) {
                    if (idx_plus > in_units - cnt_plus(i)) {
                        buffer(i)(j) := io.in(j)
                    }
                    else {
                        buffer(i)(2*idx_plus) := io.in(j)
                        idx_plus = idx_plus + 1
                    }
                }
                else {
                    buffer(i)(2*idx_minus+1) := -io.in(j)
                    idx_minus = idx_minus + 1
                }
            }
        }
        else {
            for (j <- 0 until in_units) {
                if (param(in_units * i + j) < 0) {
                    if (idx_minus > cnt_plus(i)) {
                        buffer(i)(j) := -io.in(j)
                    }
                    else {
                        buffer(i)(2*idx_minus+1) := -io.in(j)
                        idx_minus = idx_minus + 1
                    }
                }
                else {
                    buffer(i)(2*idx_plus) := io.in(j)
                    idx_plus = idx_plus + 1
                }
            }
        }
    }

    
}
*/
}