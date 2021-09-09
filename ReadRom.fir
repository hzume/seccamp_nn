;buildInfoPackage: chisel3, version: 3.0.2, scalaVersion: 2.12.3, sbtVersion: 0.13.16, builtAtString: 2018-02-05 23:26:31.979, builtAtMillis: 1517873191979
circuit ReadRom : 
  module ReadRom : 
    input clock : Clock
    input reset : UInt<1>
    output io : {flip data : SInt<8>, addr : UInt<9>, out : SInt<8>[10]}
    
    reg cnt : UInt<9>, clock with : (reset => (reset, UInt<9>("h00"))) @[ReadRom.scala 10:22]
    wire _T_33 : SInt<1>[10] @[ReadRom.scala 11:30]
    _T_33[0] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    _T_33[1] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    _T_33[2] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    _T_33[3] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    _T_33[4] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    _T_33[5] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    _T_33[6] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    _T_33[7] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    _T_33[8] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    _T_33[9] <= asSInt(UInt<1>("h00")) @[ReadRom.scala 11:30]
    reg res : SInt<1>[10], clock with : (reset => (reset, _T_33)) @[ReadRom.scala 11:22]
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
    node _T_107 = bits(cnt, 3, 0)
    res[_T_107] <= io.data @[ReadRom.scala 15:14]
    node _T_109 = neq(cnt, UInt<4>("h0a")) @[ReadRom.scala 17:14]
    when _T_109 : @[ReadRom.scala 17:25]
      node _T_111 = add(cnt, UInt<1>("h01")) @[ReadRom.scala 18:20]
      node _T_112 = tail(_T_111, 1) @[ReadRom.scala 18:20]
      cnt <= _T_112 @[ReadRom.scala 18:13]
      skip @[ReadRom.scala 17:25]
    
