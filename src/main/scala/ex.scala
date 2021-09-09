import chisel3._

class Ex extends Module {
    val io = IO(new Bundle{
        val in = Input(Vec(784, SInt(4.W)))
        val out = Output(Vec(784, SInt(4.W)))
    })
    io.out := io.in
}