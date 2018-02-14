import disassembler
import sys

def map(v0):
	def anon1(v1): 
		def anon2(f,L): 
			if len(L) == 0:
				return []
			else:
				return [f(L[0])] + ((map(f))(L[1:]))

		return anon2(v0,v1)

	return anon1
	

def main():
	print(map(lambda x: x+1)([1,2,3,4]))

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(map)
	disassembler.disassemble(main)