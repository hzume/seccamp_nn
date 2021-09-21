import chisel3._

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

class Linear_p(val in_units:Int, val out_units:Int, val param:Seq[Int], val in_w:Int, val out_w:Int) extends Module {
    val io = IO(new Bundle{
        val in = Input(Vec(in_units, SInt(in_w.W)))
        val out = Output(Vec(out_units, SInt(out_w.W)))
    })

    val buffer = Wire(Vec(out_units, Vec(in_units, SInt(out_w.W))))

    for (i <- 0 until out_units) {
        for (j <- 0 until in_units if j % 2 == 0) {
            buffer(i)(j / 2) := param(i * in_units + j).S * io.in(j) + param(i * in_units + j + 1).S * io.in(j + 1)
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

class Linear(val in_units:Int, val out_units:Int, val param:Seq[Int], val in_w:Int, val out_w:Int) extends Module {
    val io = IO(new Bundle{
        val in = Input(Vec(in_units, SInt(in_w.W)))
        val out = Output(Vec(out_units, SInt(out_w.W)))
    })

    val buffer = Wire(Vec(out_units, Vec(in_units, SInt(out_w.W))))

    for (i <- 0 until out_units) {
        for (j <- 0 until in_units) {
            if (j == 0) {
                buffer(i)(j) := param(i * in_units + j).S * io.in(j)
            }
            else {
                buffer(i)(j) := buffer(i)(j - 1) + param(i * in_units + j).S * io.in(j)
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
        }
        if (norm(i) >= 0) {
            x_hat(i)(h)(w) := c_x(i)(h)(w) << norm(i).U(4.W)
        }
        else {
            x_hat(i)(h)(w) := c_x(i)(h)(w) << (-norm(i)).U(4.W)
        }
        normed_x_hat(i)(h)(w) := x_hat(i) << weight(i).U(4.W)
        io.out(i)(h)(w) := normed_x_hat(i)(h)(w) + bias(i).S(bit_w.W)
    }

}

class ShiftBatchNorm(val num_units:Int, val weight:Seq[Int], val bias:Seq[Int], val mean:Seq[Int], val norm:Seq[Int], val bit_w:Int) extends Module {
    val io = IO(new Bundle {
        val in = Input(Vec(num_units, SInt(bit_w.W)))
        val out = Output(Vec(num_units, SInt(bit_w.W)))
    })

    val c_x = Wire(Vec(num_units, SInt(bit_w.W)))
    val x_hat = Wire(Vec(num_units, SInt(bit_w.W)))
    val normed_x_hat = Wire(Vec(num_units, SInt(bit_w.W)))
    for (i <- 0 until num_units) {
        c_x(i) := io.in(i) - mean(i).S(bit_w.W)
        if (norm(i) >= 0) {
            x_hat(i) := c_x(i) << norm(i).U(4.W)    
        }
        else {
            x_hat(i) := c_x(i) << (-norm(i)).U(4.W)
        }
        normed_x_hat(i) := x_hat(i) << weight(i).U(4.W)
        io.out(i) := normed_x_hat(i) + bias(i).S(bit_w.W) 
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

class MLP(val in_w:Int, val out_w:Int, val fc1_d: Seq[Int], val fc2_d: Seq[Int], val fc3_d: Seq[Int],
            val bn1_weight: Seq[Int], val bn1_bias: Seq[Int], val bn1_mean: Seq[Int], val bn1_norm: Seq[Int],
            val bn2_weight: Seq[Int], val bn2_bias: Seq[Int], val bn2_mean: Seq[Int], val bn2_norm: Seq[Int],
            val bn3_weight: Seq[Int], val bn3_bias: Seq[Int], val bn3_mean: Seq[Int], val bn3_norm: Seq[Int]) extends Module {
    val io = IO(new Bundle{
        val in = Input(Vec(784, SInt(in_w.W)))
        val out = Output(Vec(10, SInt(out_w.W)))
    })

    val fc1 = Module(new Linear(784, 16, fc1_d, in_w, 11))
    val bn1 = Module(new ShiftBatchNorm(16, bn1_weight, bn1_bias, bn1_mean, bn1_norm, 11))
    val bi1 = Module(new Binarize(16, 11))
    
    val fc2 = Module(new Linear(16, 16, fc2_d, 2, out_w))
    val bn2 = Module(new ShiftBatchNorm(16, bn2_weight, bn2_bias, bn2_mean, bn2_norm, out_w))
    val bi2 = Module(new Binarize(16, out_w))
    
    val fc3 = Module(new Linear(16, 10, fc3_d, 2, out_w))
    val bn3 = Module(new ShiftBatchNorm(10, bn3_weight, bn3_bias, bn3_mean, bn3_norm, out_w))

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

class MLP_p(val in_w:Int, val out_w:Int, val fc1_d: Seq[Int], val fc2_d: Seq[Int], val fc3_d: Seq[Int],
            val bn1_weight: Seq[Int], val bn1_bias: Seq[Int], val bn1_mean: Seq[Int], val bn1_norm: Seq[Int],
            val bn2_weight: Seq[Int], val bn2_bias: Seq[Int], val bn2_mean: Seq[Int], val bn2_norm: Seq[Int],
            val bn3_weight: Seq[Int], val bn3_bias: Seq[Int], val bn3_mean: Seq[Int], val bn3_norm: Seq[Int]) extends Module {
    val io = IO(new Bundle{
        val in = Input(Vec(784, SInt(in_w.W)))
        val out = Output(Vec(10, SInt(out_w.W)))
    })

    val fc1 = Module(new Linear_p(784, 16, fc1_d, in_w, 11))
    val bn1 = Module(new ShiftBatchNorm(16, bn1_weight, bn1_bias, bn1_mean, bn1_norm, 11))
    val bi1 = Module(new Binarize(16, 11))
    
    val fc2 = Module(new Linear(16, 16, fc2_d, 2, out_w))
    val bn2 = Module(new ShiftBatchNorm(16, bn2_weight, bn2_bias, bn2_mean, bn2_norm, out_w))
    val bi2 = Module(new Binarize(16, out_w))
    
    val fc3 = Module(new Linear(16, 10, fc3_d, 2, out_w))
    val bn3 = Module(new ShiftBatchNorm(10, bn3_weight, bn3_bias, bn3_mean, bn3_norm, out_w))

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

