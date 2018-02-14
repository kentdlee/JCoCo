import disassembler
import sys

def main():
	f = 8
	i = 1
	j = 1
	n = 1
	while n < f:
		n = n + 1
		tmp = j
		j = j + i
		i = tmp

	print("Fibonacci("+str(n)+") is",i)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)