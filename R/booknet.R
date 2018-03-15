booknet <- function(folder_path, vertex_names, window_length = 15, min_occurences = 0, percent_remove = 0) {
  class_name <- J("BookNet")
  book_net <- new(class_name, folder_path, as.integer(window_length))
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
