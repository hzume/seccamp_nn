;buildInfoPackage: chisel3, version: 3.0.2, scalaVersion: 2.12.3, sbtVersion: 0.13.16, builtAtString: 2018-02-05 23:26:31.979, builtAtMillis: 1517873191979
circuit TopReadRom : 
  module ReadRom : 
    input clock : Clock
    input reset : UInt<1>
    output io : {flip data : SInt<8>, addr : UInt<10>, out : SInt<8>[10]}
    
    reg cnt : UInt<10>, clock with : (reset => (reset, UInt<10>("h00"))) @[ReadRom.scala 10:22]
    wire _T_33 : SInt<8>[10] @[ReadRom.scala 11:30]
    _T_33[0] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    _T_33[1] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    _T_33[2] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    _T_33[3] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    _T_33[4] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    _T_33[5] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    _T_33[6] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    _T_33[7] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    _T_33[8] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    _T_33[9] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 11:30]
    reg res : SInt<8>[10], clock with : (reset => (reset, _T_33)) @[ReadRom.scala 11:22]
    io.out[0] <= res[0] @[ReadRom.scala 13:12]
    io.out[1] <= res[1] @[ReadRom.scala 13:12]
    io.out[2] <= res[2] @[ReadRom.scala 13:12]
    io.out[3] <= res[3] @[ReadRom.scala 13:12]
    io.out[4] <= res[4] @[ReadRom.scala 13:12]
    io.out[5] <= res[5] @[ReadRom.scala 13:12]
    io.out[6] <= res[6] @[ReadRom.scala 13:12]
    io.out[7] <= res[7] @[ReadRom.scala 13:12]
    io.out[8] <= res[8] @[ReadRom.scala 13:12]
    io.out[9] <= res[9] @[ReadRom.scala 13:12]
    io.addr <= cnt @[ReadRom.scala 14:13]
    node _T_105 = neq(cnt, UInt<4>("h0a")) @[ReadRom.scala 16:14]
    when _T_105 : @[ReadRom.scala 16:25]
      node _T_109 = bits(cnt, 3, 0)
      res[_T_109] <= io.data @[ReadRom.scala 17:18]
      node _T_111 = add(cnt, UInt<1>("h01")) @[ReadRom.scala 18:20]
      node _T_112 = tail(_T_111, 1) @[ReadRom.scala 18:20]
      cnt <= _T_112 @[ReadRom.scala 18:13]
      skip @[ReadRom.scala 16:25]
    
  module ExternalRom : 
    input clock : Clock
    input reset : UInt<1>
    output io : {flip addr : UInt<10>, data : SInt<8>}
    
    wire rom : SInt<8>[10] @[ReadRom.scala 12:20]
    rom[0] <= asSInt(UInt<8>("h0f1")) @[ReadRom.scala 12:20]
    rom[1] <= asSInt(UInt<8>("h010")) @[ReadRom.scala 12:20]
    rom[2] <= asSInt(UInt<8>("h0f0")) @[ReadRom.scala 12:20]
    rom[3] <= asSInt(UInt<8>("h0f0")) @[ReadRom.scala 12:20]
    rom[4] <= asSInt(UInt<8>("h0f")) @[ReadRom.scala 12:20]
    rom[5] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 12:20]
    rom[6] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 12:20]
    rom[7] <= asSInt(UInt<8>("h032")) @[ReadRom.scala 12:20]
    rom[8] <= asSInt(UInt<8>("h064")) @[ReadRom.scala 12:20]
    rom[9] <= asSInt(UInt<8>("h00")) @[ReadRom.scala 12:20]
    node _T_29 = bits(io.addr, 3, 0)
    io.data <= rom[_T_29] @[ReadRom.scala 13:11]
    
  module TopReadRom : 
    input clock : Clock
    input reset : UInt<1>
    output io : {out : SInt<8>[10]}
    
    inst read_rom of ReadRom @[ReadRom.scala 21:26]
    read_rom.clock <= clock
    read_rom.reset <= reset
    inst rom of ExternalRom @[ReadRom.scala 22:21]
    rom.clock <= clock
    rom.reset <= reset
    rom.io.addr <= read_rom.io.addr @[ReadRom.scala 24:17]
    read_rom.io.data <= rom.io.data @[ReadRom.scala 25:22]
    io.out[0] <= read_rom.io.out[0] @[ReadRom.scala 26:12]
    io.out[1] <= read_rom.io.out[1] @[ReadRom.scala 26:12]
    io.out[2] <= read_rom.io.out[2] @[ReadRom.scala 26:12]
    io.out[3] <= read_rom.io.out[3] @[ReadRom.scala 26:12]
    io.out[4] <= read_rom.io.out[4] @[ReadRom.scala 26:12]
    io.out[5] <= read_rom.io.out[5] @[ReadRom.scala 26:12]
    io.out[6] <= read_rom.io.out[6] @[ReadRom.scala 26:12]
    io.out[7] <= read_rom.io.out[7] @[ReadRom.scala 26:12]
    io.out[8] <= read_rom.io.out[8] @[ReadRom.scala 26:12]
    io.out[9] <= read_rom.io.out[9] @[ReadRom.scala 26:12]
    
