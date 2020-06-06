# Mexican Train Solver
This is a simple Mexican Train Solver that can generate hands or solve yours!

## Strategy
The strategy I've adopted for this solver is by mapping all possibilities with the given hand to the main domino.
Then I find the bottom nodes and see which node has the lowest hand size.
Then from that node I work the way up the tree and purge all other branches.