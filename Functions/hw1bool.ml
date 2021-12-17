type bool_expr =
  | Lit of string
  | Not of bool_expr
  | And of bool_expr * bool_expr
  | Or of bool_expr * bool_expr;;

let rec eval_expr bool_expr a b c d =
  match bool_expr with 
  | Lit expr -> if (a = expr) then c 
                else d
  | Not expr -> not (eval_expr bool_expr a b c d)
  | And (expr1,expr2) -> ((eval_expr expr1 a b c d) && (eval_expr expr2 a b c d)) 
  | Or (expr1,expr2) -> ((eval_expr expr1 a b c d) || (eval_expr expr2 a b c d));;  

let truth_table a b bool_expr = 
  [(true,true,(eval_expr bool_expr a b true true));
  (true,false,(eval_expr bool_expr a b true false));
  (false,true,(eval_expr  bool_expr a b false true));
  (false,false,(eval_expr bool_expr a b false false))];;