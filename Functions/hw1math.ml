type expr =
  | Const of int 
  | Var of string
  | Plus of {arg1 : expr ; arg2 : expr}
  | Mult of {arg1 : expr ; arg2 : expr}
  | Minus of {arg1 : expr ; arg2 : expr}
  | Div of {arg1 : expr ; arg2 : expr} ;;

let rec evaluate expr =
  match expr with
  | Const arg1 -> arg1
  | Var arg1 -> 0
  | Plus var -> (evaluate var.arg1) +  (evaluate var.arg2)
  | Mult var -> (evaluate var.arg1) * (evaluate var.arg2)
  | Minus var -> (evaluate var.arg1) - (evaluate var.arg2)
  | Div var -> (evaluate var.arg1) / (evaluate var.arg2);;