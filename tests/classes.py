import disassembler
import sys

class A:

	def __init__(self,x,y):
		self.x = x
		self.y = y

	def getX(self):
		return self.x

	def slope(self):
		return self.x / self.y


def main():
	y = 6 # must be before class definition to 
	      # reference in class B. The closure is 
	      # taken during the class definition time
	      # and not when the instance of B gets
	      # created. 

	class B:
		z = 10

		def __init__(self,x):
			self.x = y

		def getX(self):
			return self.x

	a = A(5,3)
	b = B(5)


	slope = a.slope()

	bx = b.getX()

	bx2 = B.getX(b)

	slope2 = A.slope(a)

	print(slope,bx,slope2,bx2)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(A)
	disassembler.disassemble(main)
