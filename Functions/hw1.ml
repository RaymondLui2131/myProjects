(* 1 Recursion and Higher-order Functions*)
(* Problem 1 *)
let rec pow x n =
  match n with
  |0 -> 1
  |_ -> x * (pow x (n-1));;
 
let rec float_pow x n =
  match n with
  |0 -> 1.0
  |_ -> x *. (float_pow x (n-1));;
 
(* Problem 2 *)
let rec compress x =
  match x with
  | [] -> x
  | [y] -> [y]
  | head::head2::tail -> if (head = head2) then compress (head2::tail) else (head:: compress (head2::tail));;
 
(* Problem 3 *) 
let rec remove_if list predicate = 
  match list with
  | [] -> list
  | head::tail -> if (predicate head) then remove_if tail predicate else head::(remove_if tail predicate);;

(* Problem 4 *)
let rec slice list s e = 
  match list with
  | [] -> list
  | head::tail -> if (s > e) then slice [] 0 0
                  else if (s > 0) then slice tail (s-1) (e-1) 
                  else if (e > 0) then head::(slice tail s (e-1))
                  else slice [] 0 0;;
(* Problem 5 *)
let equivs f lst =
  let rec equivs_helper f inputs e_lst =
    let rec append_eclass f e_lst elem =
      match e_lst with
      |[] -> [[elem]]
      |(h::t)::rest -> if (f elem h) then ((h::t)@[elem])::rest else [h::t]@(append_eclass f rest elem)
      |h::t -> [[]]
    in
    match inputs with
    |[] -> e_lst
    |h::t -> equivs_helper f t (append_eclass f e_lst h)
  in
  equivs_helper f lst [];;

(* Problem 6 *)
let is_prime x =
  let x = max x (x * -1) in
  let rec not_divisor d =
    (x < d * d) || (0 < x mod d && not_divisor (d + 1))
  in
  not_divisor 2;;

let goldbachpair x =
  let rec goldbach d =
    if is_prime (x - d) && is_prime d then (d, x - d)
    else goldbach (d + 1)
  in
  goldbach 2;;

(* Problem 7 *)
let rec equiv_on f g lst =
  match lst with
  | [] -> true
  | h::t -> if (f h = g h) 
      then equiv_on f g t
      else false;;

(* Problem 8 *)
let rec add_list x lst =
  match lst with 
  | [] -> [x]
  | h::t -> h::(add_list x t);;

let rec make_list cmp lst newlst =
  match lst with
  | [] -> newlst
  | h::m::t -> if (cmp h m = h) then let addH = (add_list h newlst) in (make_list cmp t addH)
             else if (cmp h m = m) then let addM = (add_list m newlst) in (make_list cmp t addM)
             else add_list h newlst
  | h::t -> add_list h newlst;;           

let rec pairwisefilter cmp lst = make_list cmp lst [];;
  
(* Problem 9 *)
let rec pow x n =
  match n with
  |0 -> 1
  |_ -> x * (pow x (n-1));;

let rec tupleFunc tuple = 
  match tuple with
  | (a,b) -> fun x -> a * (pow x b);;

let addTupleFuncToList tupleFunc newLst = tupleFunc::newLst;;

let rec plugInToFunc x list =
  match list with
  | [] -> 0
  | h::t -> (h x) + plugInToFunc x t;; 

let rec polynomial_helper list newlst x =
  match list with
  | [] -> plugInToFunc x newlst
  | h::t -> let listOfFuncs = (let tF = tupleFunc h in addTupleFuncToList tF newlst) in polynomial_helper t listOfFuncs x;;

let rec polynomial list = fun x -> (polynomial_helper list [] x);;

(* Problem 10 *)
let rec addHToPowerSet x listOfList =
  match listOfList with
  | [] -> listOfList
  | h::t -> (x::h) ::(addHToPowerSet x t);;

let rec powerset list = 
  match list with
  | [] -> [[]]
  | h::t -> (addHToPowerSet h (powerset t) ) @ powerset t ;;
  





