import disassembler
import sys

def main():
	print("type:","str", "repr")
	print("bool:",False,repr(False))
	print("int:", 6, repr(6))
	print("float:",6.2, repr(6.2))
	print("str:","Hello",repr("Hello"))

	print("print:",print,(repr(print)))
	print("iter:",iter,repr(iter))
	print("input:",input,repr(input))
	print("len:",len,repr(len))

	l = [1,"hello"]
	print("list:",l,repr(l))
	i = iter(l)
	print("list_iterator:",i,repr(i))
	t = (1, "hello")
	print("tuple:",t,repr(t))

	print("int type:", int, repr(int))

	d = { "Kent" : "Denise", 6 : 6.2}
	print("dict:",d,repr(d))
	i = iter(d)
	print("dict_keyiterator:",i,repr(i))

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

