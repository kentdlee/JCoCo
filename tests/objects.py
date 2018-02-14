import disassembler
import sys

class Dog:
	def __init__(self,name,bark):
		self.name = name
		self.bark = bark

	def woof(self):
		return self.bark

	def getName(self):
		return self.name


def main():

	d = Dog("Mesa","woof")

	# The following lines do not disassemble correctly because
	# d is an object and Dog is a class. Instead we'll write
	# an alternative form.
	#print(d.woof())
	#print(d.getName())
	print(Dog.woof(d))
	print(Dog.getName(d))
	print(d.name)
	print(d.getName())

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(Dog)
	disassembler.disassemble(main)



