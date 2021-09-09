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

module ReadRom( // @[:@3.2]
  input        clock, // @[:@4.4]
  input        reset, // @[:@5.4]
  input  [7:0] io_data, // @[:@6.4]
  output [8:0] io_addr, // @[:@6.4]
  output [7:0] io_out_0, // @[:@6.4]
  output [7:0] io_out_1, // @[:@6.4]
  output [7:0] io_out_2, // @[:@6.4]
  output [7:0] io_out_3, // @[:@6.4]
  output [7:0] io_out_4, // @[:@6.4]
  output [7:0] io_out_5, // @[:@6.4]
  output [7:0] io_out_6, // @[:@6.4]
  output [7:0] io_out_7, // @[:@6.4]
  output [7:0] io_out_8, // @[:@6.4]
  output [7:0] io_out_9 // @[:@6.4]
);
  reg [8:0] cnt; // @[ReadRom.scala 10:22:@8.4]
  reg [31:0] _RAND_0;
  reg  res_0; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_1;
  reg  res_1; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_2;
  reg  res_2; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_3;
  reg  res_3; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_4;
  reg  res_4; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_5;
  reg  res_5; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_6;
  reg  res_6; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_7;
  reg  res_7; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_8;
  reg  res_8; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_9;
  reg  res_9; // @[ReadRom.scala 11:22:@20.4]
  reg [31:0] _RAND_10;
  wire [3:0] _T_107; // @[:@32.4]
  wire  _GEN_12; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_0; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_1; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_2; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_3; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_4; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_5; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_6; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_7; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_8; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_9; // @[ReadRom.scala 15:14:@33.4]
  wire  _GEN_10; // @[ReadRom.scala 15:14:@33.4]
  wire  _T_109; // @[ReadRom.scala 17:14:@34.4]
  wire [9:0] _T_111; // @[ReadRom.scala 18:20:@36.6]
  wire [8:0] _T_112; // @[ReadRom.scala 18:20:@37.6]
  wire [8:0] _GEN_11; // @[ReadRom.scala 17:25:@35.4]
  assign _T_107 = cnt[3:0]; // @[:@32.4]
  assign _GEN_12 = io_data[0]; // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_0 = $signed(_GEN_12); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_1 = 4'h0 == _T_107 ? $signed(_GEN_0) : $signed(res_0); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_2 = 4'h1 == _T_107 ? $signed(_GEN_0) : $signed(res_1); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_3 = 4'h2 == _T_107 ? $signed(_GEN_0) : $signed(res_2); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_4 = 4'h3 == _T_107 ? $signed(_GEN_0) : $signed(res_3); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_5 = 4'h4 == _T_107 ? $signed(_GEN_0) : $signed(res_4); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_6 = 4'h5 == _T_107 ? $signed(_GEN_0) : $signed(res_5); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_7 = 4'h6 == _T_107 ? $signed(_GEN_0) : $signed(res_6); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_8 = 4'h7 == _T_107 ? $signed(_GEN_0) : $signed(res_7); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_9 = 4'h8 == _T_107 ? $signed(_GEN_0) : $signed(res_8); // @[ReadRom.scala 15:14:@33.4]
  assign _GEN_10 = 4'h9 == _T_107 ? $signed(_GEN_0) : $signed(res_9); // @[ReadRom.scala 15:14:@33.4]
  assign _T_109 = cnt != 9'ha; // @[ReadRom.scala 17:14:@34.4]
  assign _T_111 = cnt + 9'h1; // @[ReadRom.scala 18:20:@36.6]
  assign _T_112 = _T_111[8:0]; // @[ReadRom.scala 18:20:@37.6]
  assign _GEN_11 = _T_109 ? _T_112 : cnt; // @[ReadRom.scala 17:25:@35.4]
  assign io_addr = cnt;
  assign io_out_0 = {8{res_0}};
  assign io_out_1 = {8{res_1}};
  assign io_out_2 = {8{res_2}};
  assign io_out_3 = {8{res_3}};
  assign io_out_4 = {8{res_4}};
  assign io_out_5 = {8{res_5}};
  assign io_out_6 = {8{res_6}};
  assign io_out_7 = {8{res_7}};
  assign io_out_8 = {8{res_8}};
  assign io_out_9 = {8{res_9}};
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifndef verilator
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{$random}};
  cnt = _RAND_0[8:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{$random}};
  res_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{$random}};
  res_1 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{$random}};
  res_2 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{$random}};
  res_3 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{$random}};
  res_4 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{$random}};
  res_5 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{$random}};
  res_6 = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{$random}};
  res_7 = _RAND_8[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{$random}};
  res_8 = _RAND_9[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{$random}};
  res_9 = _RAND_10[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      cnt <= 9'h0;
    end else begin
      if (_T_109) begin
        cnt <= _T_112;
      end
    end
    if (reset) begin
      res_0 <= 1'sh0;
    end else begin
      if (4'h0 == _T_107) begin
        res_0 <= _GEN_0;
      end
    end
    if (reset) begin
      res_1 <= 1'sh0;
    end else begin
      if (4'h1 == _T_107) begin
        res_1 <= _GEN_0;
      end
    end
    if (reset) begin
      res_2 <= 1'sh0;
    end else begin
      if (4'h2 == _T_107) begin
        res_2 <= _GEN_0;
      end
    end
    if (reset) begin
      res_3 <= 1'sh0;
    end else begin
      if (4'h3 == _T_107) begin
        res_3 <= _GEN_0;
      end
    end
    if (reset) begin
      res_4 <= 1'sh0;
    end else begin
      if (4'h4 == _T_107) begin
        res_4 <= _GEN_0;
      end
    end
    if (reset) begin
      res_5 <= 1'sh0;
    end else begin
      if (4'h5 == _T_107) begin
        res_5 <= _GEN_0;
      end
    end
    if (reset) begin
      res_6 <= 1'sh0;
    end else begin
      if (4'h6 == _T_107) begin
        res_6 <= _GEN_0;
      end
    end
    if (reset) begin
      res_7 <= 1'sh0;
    end else begin
      if (4'h7 == _T_107) begin
        res_7 <= _GEN_0;
      end
    end
    if (reset) begin
      res_8 <= 1'sh0;
    end else begin
      if (4'h8 == _T_107) begin
        res_8 <= _GEN_0;
      end
    end
    if (reset) begin
      res_9 <= 1'sh0;
    end else begin
      if (4'h9 == _T_107) begin
        res_9 <= _GEN_0;
      end
    end
  end
endmodule
