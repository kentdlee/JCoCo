import disassembler
import sys

class Dog:
	def __init__(self,name,bark):
		self.name = name
		self.bark = bark

	def getName(self):
		return self.name

	def setName(self,name):
		self.name = name

	def speak(self):
		print(self.bark)

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