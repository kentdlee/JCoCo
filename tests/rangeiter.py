from disassembler import *
import sys

def main():
    x = input("Enter a list: ")
    lst = x.split()
       
    for i in range(len(lst)-1,-1,-1):
        print(lst[i])
     
if len(sys.argv) == 1:
	main()
else:   
	disassemble(main)