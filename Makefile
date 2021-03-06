JAVA_FILES=javafiles.txt  #with javac you can specify a file

GP_SRC = src/capps/gp/gpcreatures/*.java \
src/capps/gp/gppopulations/*.java \
src/capps/gp/gpcreatures/*.java \
src/capps/gp/gptrees/*.java \
src/capps/gp/gpexceptions/*.java \
src/capps/gp/gpglobal/*.java \
src/capps/gp/gpdrivers/*.java \
impl/capps/gp/gpcreatures/*.java \
impl/capps/gp/gptrees/*.java \
impl/capps/gp/gpgamestates/*.java \
src/capps/misc/*.java

MINICHESS_SRC = impl/minichess/*.java \
				impl/minichess/ai/*.java \
				impl/minichess/ai/threads/*.java \
				impl/minichess/play/*.java \
				impl/minichess/boards/*.java \
				impl/minichess/config/*.java \
				impl/minichess/exceptions/*.java \
				impl/minichess/heuristic/*.java \
				impl/minichess/executables/*.java

TEST_SRC = test/capps/gp/gptrees/*.java

general_evolver.jar: gp_classes
	jar vcfe general_evolver.jar capps.gp.gpdrivers.GeneralEvolver -C bin \
		capps

evolve_minichess.jar: gp_classes minichess_classes
	jar vcfe evolve_minichess.jar capps.gp.gpdrivers.EvolveMinichess -C bin \
		capps -C bin minichess

funcs_evolver.jar: gp_classes
	jar vcfe funcs_evolved.jar capps.gp.gpdrivers.EvolveFuncApproxMain -C bin \
		capps

play_minichess_gp.jar: minichess_classes gp_classes
	jar vcfe play_minichess_gp.jar minichess.executables.PlayMinichessGP \
	-C bin minichess -C bin capps

gp_classes: $(GP_SRC) $(TEST_SRC)
	javac -g -sourcepath src:test:impl -d ./bin $(GP_SRC) $(TEST_SRC)
	
minichess_classes: $(MINICHESS_SRC)
	javac -g -sourcepath src:test:impl -d ./bin $(MINICHESS_SRC)

clean: 
	rm -f -r bin/*
	rm -f *.jar
