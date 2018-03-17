#' BookNet
#'
#' Reads a directory of text files and generates
#' an adjacency matrix of characters in those text files, with weights
#' equal to the number of times the relationship appeared.
#' A connection is made when characters appear within
#' window_length tokens of each other, delimited by spaces.
#' The directory must contain one "names.txt" file which
#' consists of Java regular expressions one each line to search for, for a
#' given character, ie "Jaime|Kingslayer" would match for
#' Jaime Lannister in ASOIAF.
#'
#' @param folder_path path to a directory which contains the *.txt files (including "names.txt")
#' @param vertex_names optional - names for each row/column in the matrix, and vertex labels for `igraph`.
#' Must be specified in the same order as in names.txt. If missing labels from names.txt will be used instead.
#' @param window_length window of words to check for character occurences.
#' @param min_occurences weights in the matrix below this number will be set to zero.
#' @param percent_remove removes the bottom `percent_remove` percentage of nonzero weights.
#' If `min_occurences` is specified, those occurences will be removed first, before removing
#' the bottom percentage specified by `percent_remove`.
#'
#' @return a list, with `$adjacency_matrix` returning the labeled adjacency matrix,
#' and `$adj_graph` returning an `igraph` graph.
#' @export
#'
#' @examples
#' out <- booknet("data/ASOIAF_Folder", min_occurences = 3)
booknet <- function(folder_path, vertex_names, window_length = 15, min_occurences = 0, percent_remove = 0) {
  class_name <- J("BookNet")
  full_path <- gsub("\\\\", "//",normalizePath(folder_path))
  book_net <- new(class_name, full_path, as.integer(window_length))
  .jcall(book_net, "V", "doAll")
  out_matrix <- .jcall(book_net, "[[D", "getAdjacencyMatrix")
  to_return <- matrix(nrow = 0, ncol = length(out_matrix))
  for (row in out_matrix) {
    to_return <- rbind(to_return, .jevalArray(row))
  }

  if (missing(vertex_names)) {
    vertex_names <- .jcall(book_net, "[S", "getNames")
  }
  if (length(vertex_names) != ncol(to_return)) {
    stop("vertex_names is not the correct length!")
  }

  colnames(to_return) <- vertex_names
  rownames(to_return) <- vertex_names

  if (min_occurences != 0) {
    for (r in 1:nrow(to_return)) {
      for (c in 1:ncol(to_return)) {
        if (to_return[r,c][[1]] < min_occurences) {to_return[r,c] <- 0}
      }
    }
  }

  if (percent_remove != 0) {
    if (percent_remove < 0 | percent_remove > 1) stop("min_cap must be between 0 and 1")
    min_cap <- quantile(to_return[which(to_return > 0)], percent_remove)[[1]]
    for (r in 1:nrow(to_return)) {
      for (c in 1:ncol(to_return)) {
        if (to_return[r,c][[1]] < min_cap) {to_return[r,c] <- 0}
      }
    }
  }

  adj_graph <- graph_from_adjacency_matrix(to_return, mode="undirected", weighted = TRUE, add.colnames = 'label')
  return(list(adjacency_matrix = to_return, graph = adj_graph))
}
