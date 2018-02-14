from disassembler import *
import sys

def main():
	lst = list("hi there")
	for x in lst:
		sys.stdout.write(x+"\n")

	file = open("filetest.py")
	for line in file:
		print(line)


	file.close()

	file = open("filetest.txt","w")

	for x in lst:
		file.write(x+"\n")

	file.close()

if len(sys.argv) == 1:
    main()
else:   
    disassemble(main)