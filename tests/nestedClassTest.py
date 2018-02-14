import disassembler
import sys

class Dog:
	class Paw:
		def __init__(self):
			self.claws = True

		def scratch(self):
			return self.claws

	def __init__(self,name,bark):
		self.name = name
		self.bark = bark
		self.paws = [Dog.Paw()] * 4

	def getName(self):
		return self.name

	def setName(self,name):
		self.name = name

	def speak(self):
		print(self.bark)

def main():
	d = Dog("Mesa","woof")

	d.__init__("Testy", "bark");

	print(d.getName())

	d.speak()

	d.setName("Sequoia")

	print(d.paws[0].scratch())

	paw1 = Dog.Paw()

	print(paw1.scratch())

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(Dog)
	disassembler.disassemble(main)