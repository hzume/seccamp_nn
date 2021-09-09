import toml

def test_mlp():
    d = {}
    d["file"] = [{"type": "iyokanl1-json", "path": "MLP_converted.json", "name": "MLP"}]
    d["builtin"] = [{"type": "rom", "name": "input_rom", "in_addr_width": 1, "out_rdata_width": 4}]
    connect_d = {}
    for i in range(784):
        connect_d[f"MLP/io_in_{i}[0:3]"] = f"input_rom/rdata[{i*4}:{(i+1)*4-1}]"
    for i in range(10):
        connect_d[f"@out_{i}[0:7]"] = f"MLP/io_out_{i}[0:7]"
    
    d["connect"] = connect_d

    with open("tests/test_mlp.toml", mode="w") as f:
        f.write(toml.dumps(d))

def input_rom():
    d = {}
    input_arr = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 3, 6, 3, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 6, 6, 6, 6, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 5, 6, 6, 6, 5, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 5, 6, 6, 6, 5, 1, 6, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 6, 6, 6, 6, 6, 6, 2, 4, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 6, 6, 6, 4, 2, 6, 5, 0, 1, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 6, 6, 6, 4, -1, 1, 2, 0, -1, -1, 6, 6, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 4, 6, 5, 5, 1, -1, -1, -1, -1, -1, -1, 6, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 6, 6, 1, 0, 0, -1, -1, -1, -1, -1, -1, 6, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 6, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 5, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 6, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 6, 6, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 6, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, 6, 5, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 6, 3, -1, -1, -1, -1, -1, -1, -1, 0, 4, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 6, 5, -1, -1, -1, -1, -1, -1, 2, 6, 6, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 6, 6, 3, 0, 0, 1, 4, 5, 6, 5, 4, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 6, 6, 6, 5, 5, 6, 6, 6, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 5, 6, 6, 6, 6, 6, 5, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 3, 6, 6, 6, 3, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1]
    d["rom"] = [{"name": "input_rom", "size": 4 * 1024, "bytes": input_arr}]

    with open("tests/test_mlp_01.in", mode="w") as f:
        f.write(toml.dumps(d))

def ex():
    d = {}
    d["file"] = [{"type": "iyokanl1-json", "path": "ReadRom_converted.json", "name": "readrom"}]
    d["builtin"] = [{"type": "rom", "name": "input_rom", "in_addr_width": 10, "out_rdata_width": 4}]
    connect_d = {}
    connect_d["input_rom/addr[0:9]"] = "readrom/io_addr[0:9]"
    connect_d["readrom/io_data[0:3]"] = "input_rom/rdata[0:3]"
    connect_d["readrom/reset"] = "@reset"
    connect_d["@out[0:3]"] = "readrom/io_out[0:3]"
    
    d["connect"] = connect_d

    with open("tests/readrom.toml", mode="w") as f:
        f.write(toml.dumps(d))
    
if __name__=="__main__":
    #test_mlp()
    #input_rom()
    ex()