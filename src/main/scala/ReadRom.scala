import chisel3._

class ReadRom(val in_w:Int, val addr_w:Int, val num:Int) extends Module {
    val io = IO(new Bundle {
        val data = Input(SInt(in_w.W))
        val addr = Output(UInt(addr_w.W))
        val out = Output(Vec(num, SInt(in_w.W)))
    })

    val cnt = RegInit(0.U(addr_w.W))
    val res = RegInit(VecInit(Seq.fill(num)(0.S(in_w.W))))

    io.out := res
    io.addr := cnt

    when(cnt =/= num.U) {
        res(cnt) := io.data
        cnt := cnt + 1.U
    }
}