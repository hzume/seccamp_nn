circuit TopReadRom : @[:@2.0]
  module ReadRom : @[:@3.2]
    input clock : Clock @[:@4.4]
    input reset : UInt<1> @[:@5.4]
    input io_data : SInt<8> @[:@6.4]
    output io_addr : UInt<10> @[:@6.4]
    output io_out_0 : SInt<8> @[:@6.4]
    output io_out_1 : SInt<8> @[:@6.4]
    output io_out_2 : SInt<8> @[:@6.4]
    output io_out_3 : SInt<8> @[:@6.4]
    output io_out_4 : SInt<8> @[:@6.4]
    output io_out_5 : SInt<8> @[:@6.4]
    output io_out_6 : SInt<8> @[:@6.4]
    output io_out_7 : SInt<8> @[:@6.4]
    output io_out_8 : SInt<8> @[:@6.4]
    output io_out_9 : SInt<8> @[:@6.4]
  
    reg cnt : UInt<10>, clock with :
      reset => (UInt<1>("h0"), cnt) @[ReadRom.scala 10:22:@8.4]
    reg res_0 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_0) @[ReadRom.scala 11:22:@20.4]
    reg res_1 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_1) @[ReadRom.scala 11:22:@20.4]
    reg res_2 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_2) @[ReadRom.scala 11:22:@20.4]
    reg res_3 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_3) @[ReadRom.scala 11:22:@20.4]
    reg res_4 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_4) @[ReadRom.scala 11:22:@20.4]
    reg res_5 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_5) @[ReadRom.scala 11:22:@20.4]
    reg res_6 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_6) @[ReadRom.scala 11:22:@20.4]
    reg res_7 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_7) @[ReadRom.scala 11:22:@20.4]
    reg res_8 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_8) @[ReadRom.scala 11:22:@20.4]
    reg res_9 : SInt<8>, clock with :
      reset => (UInt<1>("h0"), res_9) @[ReadRom.scala 11:22:@20.4]
    node _T_105 = neq(cnt, UInt<4>("ha")) @[ReadRom.scala 16:14:@32.4]
    node _T_109 = bits(cnt, 3, 0) @[:@34.6]
    node _GEN_0 = io_data @[ReadRom.scala 17:18:@35.6]
    node _GEN_1 = mux(and(UInt<1>("h1"), eq(UInt<1>("h0"), _T_109)), _GEN_0, res_0) @[ReadRom.scala 17:18:@35.6]
    node _GEN_2 = mux(and(UInt<1>("h1"), eq(UInt<1>("h1"), _T_109)), _GEN_0, res_1) @[ReadRom.scala 17:18:@35.6]
    node _GEN_3 = mux(and(UInt<1>("h1"), eq(UInt<2>("h2"), _T_109)), _GEN_0, res_2) @[ReadRom.scala 17:18:@35.6]
    node _GEN_4 = mux(and(UInt<1>("h1"), eq(UInt<2>("h3"), _T_109)), _GEN_0, res_3) @[ReadRom.scala 17:18:@35.6]
    node _GEN_5 = mux(and(UInt<1>("h1"), eq(UInt<3>("h4"), _T_109)), _GEN_0, res_4) @[ReadRom.scala 17:18:@35.6]
    node _GEN_6 = mux(and(UInt<1>("h1"), eq(UInt<3>("h5"), _T_109)), _GEN_0, res_5) @[ReadRom.scala 17:18:@35.6]
    node _GEN_7 = mux(and(UInt<1>("h1"), eq(UInt<3>("h6"), _T_109)), _GEN_0, res_6) @[ReadRom.scala 17:18:@35.6]
    node _GEN_8 = mux(and(UInt<1>("h1"), eq(UInt<3>("h7"), _T_109)), _GEN_0, res_7) @[ReadRom.scala 17:18:@35.6]
    node _GEN_9 = mux(and(UInt<1>("h1"), eq(UInt<4>("h8"), _T_109)), _GEN_0, res_8) @[ReadRom.scala 17:18:@35.6]
    node _GEN_10 = mux(and(UInt<1>("h1"), eq(UInt<4>("h9"), _T_109)), _GEN_0, res_9) @[ReadRom.scala 17:18:@35.6]
    node _T_111 = add(cnt, UInt<1>("h1")) @[ReadRom.scala 18:20:@36.6]
    node _T_112 = tail(_T_111, 1) @[ReadRom.scala 18:20:@37.6]
    node _GEN_11 = mux(_T_105, _GEN_1, res_0) @[ReadRom.scala 16:25:@33.4]
    node _GEN_12 = mux(_T_105, _GEN_2, res_1) @[ReadRom.scala 16:25:@33.4]
    node _GEN_13 = mux(_T_105, _GEN_3, res_2) @[ReadRom.scala 16:25:@33.4]
    node _GEN_14 = mux(_T_105, _GEN_4, res_3) @[ReadRom.scala 16:25:@33.4]
    node _GEN_15 = mux(_T_105, _GEN_5, res_4) @[ReadRom.scala 16:25:@33.4]
    node _GEN_16 = mux(_T_105, _GEN_6, res_5) @[ReadRom.scala 16:25:@33.4]
    node _GEN_17 = mux(_T_105, _GEN_7, res_6) @[ReadRom.scala 16:25:@33.4]
    node _GEN_18 = mux(_T_105, _GEN_8, res_7) @[ReadRom.scala 16:25:@33.4]
    node _GEN_19 = mux(_T_105, _GEN_9, res_8) @[ReadRom.scala 16:25:@33.4]
    node _GEN_20 = mux(_T_105, _GEN_10, res_9) @[ReadRom.scala 16:25:@33.4]
    node _GEN_21 = mux(_T_105, _T_112, cnt) @[ReadRom.scala 16:25:@33.4]
    node _T_33_0 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    node _T_33_1 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    node _T_33_2 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    node _T_33_3 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    node _T_33_4 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    node _T_33_5 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    node _T_33_6 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    node _T_33_7 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    node _T_33_8 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    node _T_33_9 = asSInt(UInt<8>("h0")) @[ReadRom.scala 11:30:@9.4]
    io_addr <= cnt
    io_out_0 <= res_0
    io_out_1 <= res_1
    io_out_2 <= res_2
    io_out_3 <= res_3
    io_out_4 <= res_4
    io_out_5 <= res_5
    io_out_6 <= res_6
    io_out_7 <= res_7
    io_out_8 <= res_8
    io_out_9 <= res_9
    cnt <= mux(reset, UInt<10>("h0"), _GEN_21)
    res_0 <= mux(reset, _T_33_0, _GEN_11)
    res_1 <= mux(reset, _T_33_1, _GEN_12)
    res_2 <= mux(reset, _T_33_2, _GEN_13)
    res_3 <= mux(reset, _T_33_3, _GEN_14)
    res_4 <= mux(reset, _T_33_4, _GEN_15)
    res_5 <= mux(reset, _T_33_5, _GEN_16)
    res_6 <= mux(reset, _T_33_6, _GEN_17)
    res_7 <= mux(reset, _T_33_7, _GEN_18)
    res_8 <= mux(reset, _T_33_8, _GEN_19)
    res_9 <= mux(reset, _T_33_9, _GEN_20)

  module ExternalRom : @[:@41.2]
    input clock : Clock @[:@42.4]
    input reset : UInt<1> @[:@43.4]
    input io_addr : UInt<10> @[:@44.4]
    output io_data : SInt<8> @[:@44.4]
  
    node _T_29 = bits(io_addr, 3, 0) @[:@57.4]
    node rom_1 = asSInt(UInt<8>("h10")) @[ReadRom.scala 12:20:@46.4]
    node rom_0 = asSInt(UInt<8>("hf1")) @[ReadRom.scala 12:20:@46.4]
    node _GEN_1 = mux(and(UInt<1>("h1"), eq(UInt<1>("h1"), _T_29)), rom_1, rom_0) @[ReadRom.scala 13:11:@58.4]
    node rom_2 = asSInt(UInt<8>("hf0")) @[ReadRom.scala 12:20:@46.4]
    node _GEN_2 = mux(and(UInt<1>("h1"), eq(UInt<2>("h2"), _T_29)), rom_2, _GEN_1) @[ReadRom.scala 13:11:@58.4]
    node rom_3 = asSInt(UInt<8>("hf0")) @[ReadRom.scala 12:20:@46.4]
    node _GEN_3 = mux(and(UInt<1>("h1"), eq(UInt<2>("h3"), _T_29)), rom_3, _GEN_2) @[ReadRom.scala 13:11:@58.4]
    node rom_4 = asSInt(UInt<8>("hf")) @[ReadRom.scala 12:20:@46.4]
    node _GEN_4 = mux(and(UInt<1>("h1"), eq(UInt<3>("h4"), _T_29)), rom_4, _GEN_3) @[ReadRom.scala 13:11:@58.4]
    node rom_5 = asSInt(UInt<8>("h0")) @[ReadRom.scala 12:20:@46.4]
    node _GEN_5 = mux(and(UInt<1>("h1"), eq(UInt<3>("h5"), _T_29)), rom_5, _GEN_4) @[ReadRom.scala 13:11:@58.4]
    node rom_6 = asSInt(UInt<8>("h0")) @[ReadRom.scala 12:20:@46.4]
    node _GEN_6 = mux(and(UInt<1>("h1"), eq(UInt<3>("h6"), _T_29)), rom_6, _GEN_5) @[ReadRom.scala 13:11:@58.4]
    node rom_7 = asSInt(UInt<8>("h32")) @[ReadRom.scala 12:20:@46.4]
    node _GEN_7 = mux(and(UInt<1>("h1"), eq(UInt<3>("h7"), _T_29)), rom_7, _GEN_6) @[ReadRom.scala 13:11:@58.4]
    node rom_8 = asSInt(UInt<8>("h64")) @[ReadRom.scala 12:20:@46.4]
    node _GEN_8 = mux(and(UInt<1>("h1"), eq(UInt<4>("h8"), _T_29)), rom_8, _GEN_7) @[ReadRom.scala 13:11:@58.4]
    node rom_9 = asSInt(UInt<8>("h0")) @[ReadRom.scala 12:20:@46.4]
    node _GEN_9 = mux(and(UInt<1>("h1"), eq(UInt<4>("h9"), _T_29)), rom_9, _GEN_8) @[ReadRom.scala 13:11:@58.4]
    node _GEN_0 = _GEN_9 @[ReadRom.scala 13:11:@58.4]
    io_data <= _GEN_0

  module TopReadRom : @[:@60.2]
    input clock : Clock @[:@61.4]
    input reset : UInt<1> @[:@62.4]
    output io_out_0 : SInt<8> @[:@63.4]
    output io_out_1 : SInt<8> @[:@63.4]
    output io_out_2 : SInt<8> @[:@63.4]
    output io_out_3 : SInt<8> @[:@63.4]
    output io_out_4 : SInt<8> @[:@63.4]
    output io_out_5 : SInt<8> @[:@63.4]
    output io_out_6 : SInt<8> @[:@63.4]
    output io_out_7 : SInt<8> @[:@63.4]
    output io_out_8 : SInt<8> @[:@63.4]
    output io_out_9 : SInt<8> @[:@63.4]
  
    inst read_rom of ReadRom @[ReadRom.scala 21:26:@65.4]
    inst rom of ExternalRom @[ReadRom.scala 22:21:@68.4]
    io_out_0 <= read_rom.io_out_0
    io_out_1 <= read_rom.io_out_1
    io_out_2 <= read_rom.io_out_2
    io_out_3 <= read_rom.io_out_3
    io_out_4 <= read_rom.io_out_4
    io_out_5 <= read_rom.io_out_5
    io_out_6 <= read_rom.io_out_6
    io_out_7 <= read_rom.io_out_7
    io_out_8 <= read_rom.io_out_8
    io_out_9 <= read_rom.io_out_9
    read_rom.clock <= clock
    read_rom.reset <= reset
    read_rom.io_data <= rom.io_data
    rom.clock <= clock
    rom.reset <= reset
    rom.io_addr <= read_rom.io_addr
