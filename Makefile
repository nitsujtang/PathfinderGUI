JFLAGS = -g
JC = javac
JVM = java
FILE = PathFinderController

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = PathFinder.java \
          PathFinderController.java \
					Node.java \
					NodeComparator.java

MAIN = PathFinderController

run: classes
	$(JVM) $(MAIN)

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
