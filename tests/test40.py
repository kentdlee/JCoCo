import disassembler
import sys

def main():

    s = input("Please enter some words:")
    lst = s.split()
    for i in range(len(lst)//2):
        tmp = lst[i]
        lst[i] = lst[len(lst)-i-1]
        lst[len(lst)-i-1] = tmp

    print(lst)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)
