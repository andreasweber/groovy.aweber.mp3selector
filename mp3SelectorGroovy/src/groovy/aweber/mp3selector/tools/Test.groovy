package groovy.aweber.mp3selector.tools

class Test {

	def var = "test"
	String var2 = "test2"
	File f = new File("")

	void method() {
		println("method")
	}

	def closure = {
		//println("closure(): ${var}" )
	}

	def createClosure() {
		println("createClosure()")
		def closure = {
			println("closure(): ${var}.length() ${var2.length()} ${f.exists()}" )
			println("closure(): " + f.exists() )
			if (f.exists()) {
				println("haha")
			}
			method()
			if ("bla") {
				println("bla")
			}
		}
		return closure
	}

	static main(args) {
		def c = new Test().createClosure()
		c()
	}
}
