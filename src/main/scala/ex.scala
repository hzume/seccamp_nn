import chisel3._

class Rom extends Module{
    val io = IO(new Bundle{
        val romAddr = Input(UInt(16.W))
        val romData = Output(UInt(1.W))
    })

    val rom = VecInit(List(0, 1).map(_.asUInt(1.W)))

    io.romData := rom(io.romAddr)
}