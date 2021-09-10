import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class ExternalRom(val addr_w: Int, val d: Seq[Int]) extends Module {
  val io = IO(new Bundle{
	  val addr = Input(UInt(addr_w.W))
	  val data = Output(SInt(8.W))
  })

  val rom = VecInit(d map (x=> x.S(8.W)))
  io.data := rom(io.addr)
}

class TopReadRom(val d: Seq[Int], val in_w:Int, val addr_w:Int, val num:Int) extends Module {
    val io = IO(new Bundle {
        val out = Output(Vec(num, SInt(in_w.W)))
    })

    val read_rom = Module(new ReadRom(in_w, addr_w, num))
    val rom = Module(new ExternalRom(addr_w, d))

    rom.io.addr := read_rom.io.addr
    read_rom.io.data := rom.io.data
    io.out := read_rom.io.out
}

class ReadRomTester(c: TopReadRom) extends PeekPokeTester(c){
    step(11)
    for (i <- 0 until 10) {
        println(s"out[$i] = ${peek(c.io.out(i))}")
    }
}

class ReadRomSpec extends AnyFreeSpec with Matchers{
    val input_array = Array(-15, 16, -16, -16, 15, 0, 0, 50, 100, 0)
    Driver.execute(Array("--backend-name", "firrtl"), () => new TopReadRom(input_array, 8, 10, 10)) {
        c => new ReadRomTester(c)
    }
}