import subprocess
from typing import List, Dict


def parse_output(output_lines: List[str]) -> Dict[str, str]:
    output_lines = [line.replace('\n', '') for line in output_lines]
    return {
        'state': output_lines[0],
        'path_length': output_lines[1],
        'path': output_lines[2],
        'execution_time': int(output_lines[3].replace('ms', ''))
    }


def calculate_median(seq: List[int]) -> float:
    seq_sorted = sorted(seq)
    seq_sorted_len = len(seq_sorted)

    if seq_sorted_len % 2 == 0:
        return (seq_sorted[int(seq_sorted_len / 2)-1] + seq_sorted[int(seq_sorted_len / 2)]) / 2
    else:
        return seq_sorted[int(seq_sorted_len / 2)]


def calculate_mode(seq: List[int]) -> int:
    values = {}

    for v in seq:
        values[v] = 1 if v not in values else values[v] + 1

    values = {k: v for k, v in sorted(values.items(), key=lambda item: item[1])}

    for j in range(len(values)):
        if (list(values.values()).count(values[j])) > 1:
            return values[j]

    return 0


def calculate_mean(seq: List[int]) -> float:
    return sum(seq)/len(seq)


results = {
    'states': [],
    'exec_t_a_star': [],
    'exec_t_backtrack': [],
}

for i in range(4):
    print('Running next...')

    try:
        process = subprocess.check_output('java -jar AIAssignment1.jar', timeout=300)
    except subprocess.TimeoutExpired:
        print('Timeout exceeded after 5 minutes')
        continue

    with open('outputAStar.txt') as f:
        lines = f.readlines()
        output = parse_output(lines)

        results['states'].append(output['state'])
        results['exec_t_a_star'].append(output['execution_time'])

    with open('outputBacktracking.txt') as f:
        lines = f.readlines()
        output = parse_output(lines)

        results['states'].append(output['state'])
        results['exec_t_backtrack'].append(output['execution_time'])

print(results)

print('Mean A*', calculate_mean(results['exec_t_a_star']))
print('Mean Backtracking', calculate_mean(results['exec_t_backtrack']))

print('Median A*', calculate_median(results['exec_t_a_star']))
print('Median Backtracking', calculate_median(results['exec_t_backtrack']))
