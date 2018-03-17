 # BookNet

Inspired by [A Network of Thrones](https://www.macalester.edu/~abeverid/thrones.html), by one of my professors at Macalester College.

A multithreaded Java program with a front end in R. Reads a directory of text files and generates
an adjacency matrix/igraph graph of characters in those text files, with weights
equal to the number of times the relationship appeared.
A connection is made when characters appear within
`window_length` tokens of each other, delimited by spaces.
The directory must contain one `names.txt` file which
consists of Java regular expressions one each line to search for, for a
given character, ie `Jaime|Kingslayer` would match for
Jaime Lannister in ASOIAF.

## Suggested Usage

Create a folder and put every text file that you want to analyze in the directory. 
Additionally, add a file `names.txt` which has a regular expression on each line, 
where each regex corresponds to a single "character" to look for.

I would recommend breaking a book into a text file for each chapter. Note that 
each file runs on a different thread, so keep that in mind to take advantage of 
parallel processing.

While the `booknet` function returns a list containing an adjacency matrix and 
an `igraph` graph, I would highly recommend writing the adjacency matrix to a 
csv, which can be done simply with `write.csv(out$adjacency_matrix, file = "out.csv")` 
and importing this csv into [Gephi](https://gephi.org/), a much easier to use and 
interactive graphing platform

## The Algorithm

Start the following for each text file on a new thread:

For a given file, will iterate along on a `window_length`-sized token
window. On each iteration, will put every character regular
expression from `names.txt` that it sees in the window into a set. Every possible
pairing of these characters will be generated, and each pairing
will be incremented by one in the adjacency matrix.

Additionally, a map of how many tokens ago a given token pair was seen
is kept. If a pair is seen, the count is set to `window_length`. Then, at
each iteration, every count is decremented by one. This ensures that
if a pair is "seen," it cannot be seen again for `window_length` tokens.

## Quick Example

Here's a graph of a few characters in *A Game of Thrones* that I made in Gephi, character names from [here](https://www.reddit.com/r/asoiaf/comments/2g3p7s/spoilers_all_ive_listed_and_counted_every/). Note, I didn't take care in crafting 
regular expressions which would match every alias for a character in every condition (ie beginning of quotes, etc).
My regular expressions were simply a space, then the most common single word name for them. ie Khal Drogo was simply 
` Drogo`. Notice how Bran is the center, and Daenerys/that crew is separate from the rest of the graph

![GoT Graph](https://mhmdmodan.com/imgs/got_graph.png)
