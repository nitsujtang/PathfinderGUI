JFLAGS = -g
JC = javac
JVM = java -cp
FILE = src/ PathFinderController

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $ src/*.java

CLASSES = src/AStarFinder.java \
          src/PathFinderController.java \
					src/Node.java \
					src/NodeComparator.java

MAIN = src/ PathFinderController

run: classes
	$(JVM) $(MAIN)

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
