import disassembler
import sys

class Dog:
	# to run this test, step over code with debugger into this __init__
	# method and print the operand stack (option "a") to examine contents.
	# It should attempt to call the __str__ method below when the operand
	# stack is printed. Then the result will be that a Dog reference
	# will be printed instead.
	def __init__(self,name,bark):
		self.name = name
		self.bark = bark

	def getName(self):
		return self.name

	def setName(self,name):
		self.name = name

	def speak(self):
		print(self.bark)

	def __str__(self):
		return "Dog(" + self.name + "," + self.bark + ")"

def main():
	d = Dog("Mesa","woof")

	print(d.getName())

	d.speak()

	d.setName("Sequoia")

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(Dog)
	disassembler.disassemble(main)
