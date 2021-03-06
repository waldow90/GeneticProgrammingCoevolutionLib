#sample config file for a human v. human minichess game

PLAY_INTERFACE minichess.play.PlayMinichess
WHITE_AI minichess.ai.HumanPlayer
BLACK_AI minichess.ai.AbNegaMaxID
WHITE_HEURISTIC minichess.heuristic.PointHeuristic
BLACK_HEURISTIC capps.gp.gpcreatures.MinichessCreature

TIME_PER_MOVE 1000
ITERATIVE_DEEP_MIN_DEPTH 4
REVERSE_DISPLAY false
