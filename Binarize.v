`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif

module Binarize( // @[:@3.2]
  input         clock, // @[:@4.4]
  input         reset, // @[:@5.4]
  input  [10:0] io_in_0, // @[:@6.4]
  input  [10:0] io_in_1, // @[:@6.4]
  input  [10:0] io_in_2, // @[:@6.4]
  input  [10:0] io_in_3, // @[:@6.4]
  input  [10:0] io_in_4, // @[:@6.4]
  input  [10:0] io_in_5, // @[:@6.4]
  input  [10:0] io_in_6, // @[:@6.4]
  input  [10:0] io_in_7, // @[:@6.4]
  input  [10:0] io_in_8, // @[:@6.4]
  input  [10:0] io_in_9, // @[:@6.4]
  input  [10:0] io_in_10, // @[:@6.4]
  input  [10:0] io_in_11, // @[:@6.4]
  input  [10:0] io_in_12, // @[:@6.4]
  input  [10:0] io_in_13, // @[:@6.4]
  input  [10:0] io_in_14, // @[:@6.4]
  input  [10:0] io_in_15, // @[:@6.4]
  output [1:0]  io_out_0, // @[:@6.4]
  output [1:0]  io_out_1, // @[:@6.4]
  output [1:0]  io_out_2, // @[:@6.4]
  output [1:0]  io_out_3, // @[:@6.4]
  output [1:0]  io_out_4, // @[:@6.4]
  output [1:0]  io_out_5, // @[:@6.4]
  output [1:0]  io_out_6, // @[:@6.4]
  output [1:0]  io_out_7, // @[:@6.4]
  output [1:0]  io_out_8, // @[:@6.4]
  output [1:0]  io_out_9, // @[:@6.4]
  output [1:0]  io_out_10, // @[:@6.4]
  output [1:0]  io_out_11, // @[:@6.4]
  output [1:0]  io_out_12, // @[:@6.4]
  output [1:0]  io_out_13, // @[:@6.4]
  output [1:0]  io_out_14, // @[:@6.4]
  output [1:0]  io_out_15 // @[:@6.4]
);
  wire  _T_45; // @[Modules.scala 130:24:@8.4]
  wire [1:0] _GEN_0; // @[Modules.scala 130:32:@9.4]
  wire  _T_49; // @[Modules.scala 130:24:@15.4]
  wire [1:0] _GEN_1; // @[Modules.scala 130:32:@16.4]
  wire  _T_53; // @[Modules.scala 130:24:@22.4]
  wire [1:0] _GEN_2; // @[Modules.scala 130:32:@23.4]
  wire  _T_57; // @[Modules.scala 130:24:@29.4]
  wire [1:0] _GEN_3; // @[Modules.scala 130:32:@30.4]
  wire  _T_61; // @[Modules.scala 130:24:@36.4]
  wire [1:0] _GEN_4; // @[Modules.scala 130:32:@37.4]
  wire  _T_65; // @[Modules.scala 130:24:@43.4]
  wire [1:0] _GEN_5; // @[Modules.scala 130:32:@44.4]
  wire  _T_69; // @[Modules.scala 130:24:@50.4]
  wire [1:0] _GEN_6; // @[Modules.scala 130:32:@51.4]
  wire  _T_73; // @[Modules.scala 130:24:@57.4]
  wire [1:0] _GEN_7; // @[Modules.scala 130:32:@58.4]
  wire  _T_77; // @[Modules.scala 130:24:@64.4]
  wire [1:0] _GEN_8; // @[Modules.scala 130:32:@65.4]
  wire  _T_81; // @[Modules.scala 130:24:@71.4]
  wire [1:0] _GEN_9; // @[Modules.scala 130:32:@72.4]
  wire  _T_85; // @[Modules.scala 130:24:@78.4]
  wire [1:0] _GEN_10; // @[Modules.scala 130:32:@79.4]
  wire  _T_89; // @[Modules.scala 130:24:@85.4]
  wire [1:0] _GEN_11; // @[Modules.scala 130:32:@86.4]
  wire  _T_93; // @[Modules.scala 130:24:@92.4]
  wire [1:0] _GEN_12; // @[Modules.scala 130:32:@93.4]
  wire  _T_97; // @[Modules.scala 130:24:@99.4]
  wire [1:0] _GEN_13; // @[Modules.scala 130:32:@100.4]
  wire  _T_101; // @[Modules.scala 130:24:@106.4]
  wire [1:0] _GEN_14; // @[Modules.scala 130:32:@107.4]
  wire  _T_105; // @[Modules.scala 130:24:@113.4]
  wire [1:0] _GEN_15; // @[Modules.scala 130:32:@114.4]
  assign _T_45 = $signed(io_in_0) >= $signed(11'sh0); // @[Modules.scala 130:24:@8.4]
  assign _GEN_0 = _T_45 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@9.4]
  assign _T_49 = $signed(io_in_1) >= $signed(11'sh0); // @[Modules.scala 130:24:@15.4]
  assign _GEN_1 = _T_49 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@16.4]
  assign _T_53 = $signed(io_in_2) >= $signed(11'sh0); // @[Modules.scala 130:24:@22.4]
  assign _GEN_2 = _T_53 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@23.4]
  assign _T_57 = $signed(io_in_3) >= $signed(11'sh0); // @[Modules.scala 130:24:@29.4]
  assign _GEN_3 = _T_57 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@30.4]
  assign _T_61 = $signed(io_in_4) >= $signed(11'sh0); // @[Modules.scala 130:24:@36.4]
  assign _GEN_4 = _T_61 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@37.4]
  assign _T_65 = $signed(io_in_5) >= $signed(11'sh0); // @[Modules.scala 130:24:@43.4]
  assign _GEN_5 = _T_65 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@44.4]
  assign _T_69 = $signed(io_in_6) >= $signed(11'sh0); // @[Modules.scala 130:24:@50.4]
  assign _GEN_6 = _T_69 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@51.4]
  assign _T_73 = $signed(io_in_7) >= $signed(11'sh0); // @[Modules.scala 130:24:@57.4]
  assign _GEN_7 = _T_73 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@58.4]
  assign _T_77 = $signed(io_in_8) >= $signed(11'sh0); // @[Modules.scala 130:24:@64.4]
  assign _GEN_8 = _T_77 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@65.4]
  assign _T_81 = $signed(io_in_9) >= $signed(11'sh0); // @[Modules.scala 130:24:@71.4]
  assign _GEN_9 = _T_81 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@72.4]
  assign _T_85 = $signed(io_in_10) >= $signed(11'sh0); // @[Modules.scala 130:24:@78.4]
  assign _GEN_10 = _T_85 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@79.4]
  assign _T_89 = $signed(io_in_11) >= $signed(11'sh0); // @[Modules.scala 130:24:@85.4]
  assign _GEN_11 = _T_89 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@86.4]
  assign _T_93 = $signed(io_in_12) >= $signed(11'sh0); // @[Modules.scala 130:24:@92.4]
  assign _GEN_12 = _T_93 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@93.4]
  assign _T_97 = $signed(io_in_13) >= $signed(11'sh0); // @[Modules.scala 130:24:@99.4]
  assign _GEN_13 = _T_97 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@100.4]
  assign _T_101 = $signed(io_in_14) >= $signed(11'sh0); // @[Modules.scala 130:24:@106.4]
  assign _GEN_14 = _T_101 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@107.4]
  assign _T_105 = $signed(io_in_15) >= $signed(11'sh0); // @[Modules.scala 130:24:@113.4]
  assign _GEN_15 = _T_105 ? $signed(2'sh1) : $signed(-2'sh1); // @[Modules.scala 130:32:@114.4]
  assign io_out_0 = _GEN_0;
  assign io_out_1 = _GEN_1;
  assign io_out_2 = _GEN_2;
  assign io_out_3 = _GEN_3;
  assign io_out_4 = _GEN_4;
  assign io_out_5 = _GEN_5;
  assign io_out_6 = _GEN_6;
  assign io_out_7 = _GEN_7;
  assign io_out_8 = _GEN_8;
  assign io_out_9 = _GEN_9;
  assign io_out_10 = _GEN_10;
  assign io_out_11 = _GEN_11;
  assign io_out_12 = _GEN_12;
  assign io_out_13 = _GEN_13;
  assign io_out_14 = _GEN_14;
  assign io_out_15 = _GEN_15;
endmodule
