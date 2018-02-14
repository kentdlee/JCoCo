import disassembler
import sys

class Animal:
	def __init__(self,name):
		self.name = name
		self.food = 0

	def eat(self):
		self.food = self.food + 1

	def speak(self):
		print(self.name, "is an animal")

class Dog(Animal):
	def __init__(self,name):
		super().__init__(name)

	def speak(self):
		print(self.name, "says woof!")

def main():
	mesa = Dog("Mesa")

	mesa.eat()
	mesa.speak()

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(Animal)
	disassembler.disassemble(Dog)
	disassembler.disassemble(main)
