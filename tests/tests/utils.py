from typing import List, Tuple
from math import sqrt
from random import randint


def mean(seq: List[float]) -> float:
    return sum(seq) / len(seq)


def mode(seq: List[float]) -> float:
    value_counter = {}

    for v in seq:
        value_counter[v] = 1 if v not in value_counter else value_counter[v] + 1

    value_counter = {k: v for k, v in sorted(value_counter.items(), key=lambda item: item[1], reverse=True)}

    if list(value_counter.values())[0] == 1:
        return -1

    return list(value_counter.keys())[0]


def median(seq: List[float]) -> float:
    seq_sorted = sorted(seq)
    seq_sorted_len = len(seq_sorted)

    if seq_sorted_len % 2 == 0:
        return (seq_sorted[int(seq_sorted_len / 2) - 1] + seq_sorted[int(seq_sorted_len / 2)]) / 2
    else:
        return seq_sorted[int(seq_sorted_len / 2)]


def standard_deviation(seq: List[float]) -> float:
    if len(seq) == 1:
        return -1

    m = mean(seq)
    return sqrt(sum((v - m)**2 for v in seq) / (len(seq) - 1))


def coordinates_ok(coord: Tuple[int, int]) -> bool:
    return 0 <= coord[0] <= 8 and 0 <= coord[1] <= 8


def generate_coordinates() -> Tuple[int, int]:
    return randint(0, 8), randint(0, 8)


def mob_in_danger(mob: Tuple[int, int], mob_danger: Tuple[int, int], mob_type: str) -> bool:
    if coordinates_ok((mob_danger[1]+1, mob_danger[0])) and mob_danger == (mob[1]+1, mob[0]):
        return True

    if coordinates_ok((mob_danger[1]-1, mob_danger[0])) and mob_danger == (mob[1]-1, mob[0]):
        return True

    if coordinates_ok((mob_danger[1], mob_danger[0]-1)) and mob_danger == (mob[1], mob[0]-1):
        return True

    if coordinates_ok((mob_danger[1], mob_danger[0]+1)) and mob_danger == (mob[1], mob[0]+1):
        return True

    if mob_type == 'davy':
        if coordinates_ok((mob_danger[1]+1, mob_danger[0]+1)) and mob_danger == (mob[1]+1, mob[0]+1):
            return True

        if coordinates_ok((mob_danger[1]-1, mob_danger[0]-1)) and mob_danger == (mob[1]-1, mob[0]-1):
            return True

        if coordinates_ok((mob_danger[1]+1, mob_danger[0]-1)) and mob_danger == (mob[1]+1, mob[0]-1):
            return True

        if coordinates_ok((mob_danger[1]-1, mob_danger[0]+1)) and mob_danger == (mob[1]-1, mob[0]+1):
            return True

    return False


def generate_actors() -> List[tuple]:
    jack = generate_coordinates()
    chest = generate_coordinates()
    tortuga = generate_coordinates()
    rock = generate_coordinates()

    while rock == jack or rock == chest or rock == tortuga:
        rock = generate_coordinates()

    davy = generate_coordinates()

    while davy == jack\
            or davy == chest\
            or davy == tortuga\
            or davy == rock\
            or mob_in_danger(tortuga, davy, 'davy')\
            or mob_in_danger(chest, davy, 'davy')\
            or mob_in_danger(jack, davy, 'davy'):
        davy = generate_coordinates()

    kraken = generate_coordinates()

    while kraken == jack\
            or kraken == chest\
            or kraken == tortuga\
            or kraken == davy\
            or mob_in_danger(tortuga, kraken, 'kraken')\
            or mob_in_danger(chest, kraken, 'kraken')\
            or mob_in_danger(jack, kraken, 'kraken'):
        kraken = generate_coordinates()

    return [jack, davy, kraken, rock, chest, tortuga]
