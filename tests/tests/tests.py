import subprocess
from typing import List, Dict
from utils import mean, mode, median, standard_deviation, generate_actors


TESTS_NUMBER = 1000
TIMEOUT = 300  # in seconds


def parse_output(output_lines: List[str]) -> Dict[str, str]:
    output_lines = [line.replace('\n', '') for line in output_lines]

    if output_lines[0] == 'Win':
        return {
            'state': output_lines[0],
            'path_length': output_lines[1],
            'path': output_lines[2],
            'execution_time': float(output_lines[3].replace('ms', ''))
        }

    return {
        'state': output_lines[0],
        'execution_time': float(output_lines[1].replace('ms', ''))
    }


results = {
    'states_a_star': [],
    'exec_t_a_star': [],
    'states_backtrack': [],
    'exec_t_backtrack': [],
}


for i in range(TESTS_NUMBER):
    mobs = generate_actors()

    with open('input.txt', 'w') as f:
        for j in range(len(mobs)):
            f.write('[' + str(mobs[j][0]) + ',' + str(mobs[j][1]) + ']')

            if j < len(mobs) - -1:
                f.write(' ')

        f.write('\n1\n')

    print(f'Running next... [{i+1}/{TESTS_NUMBER}]')

    try:
        process = subprocess.check_output('java -jar AIAssignment1.jar', timeout=TIMEOUT)
    except subprocess.TimeoutExpired:
        print(f'Time exceeded after {TIMEOUT} seconds for test #{i}')
        continue

    with open('outputAStar.txt') as f:
        lines = f.readlines()
        output = parse_output(lines)

        results['states_a_star'].append(output['state'])
        results['exec_t_a_star'].append(output['execution_time'])

    with open('outputBacktracking.txt') as f:
        lines = f.readlines()
        output = parse_output(lines)

        results['states_backtrack'].append(output['state'])
        results['exec_t_backtrack'].append(output['execution_time'])


print()
print(results)
print('Total tests passed:', len(results['states_a_star']))
print()

print('Statistics for A*:')
print('  Mean:', mean(results['exec_t_a_star']))

m = mode(results['exec_t_a_star'])
print('  Mode:', 'No mode exists' if m == -1 else m)
print('  Median:', median(results['exec_t_a_star']))

sd = standard_deviation(results['exec_t_a_star'])
print('  Standard deviation:', 'No standard deviation' if sd == -1 else sd)
print('  Number of loses', results['states_a_star'].count('Lose'))
print('  Number of wins', results['states_a_star'].count('Win'))

print()

print('Statistics for Backtracking:')
print('  Mean:', mean(results['exec_t_backtrack']))

m = mode(results['exec_t_a_star'])
print('  Mode:', 'No mode exists' if m == -1 else m)
print('  Median:', median(results['exec_t_backtrack']))

sd = standard_deviation(results['exec_t_backtrack'])
print('  Standard deviation:', 'No standard deviation' if sd == -1 else sd)
print('  Number of loses', results['states_backtrack'].count('Lose'))
print('  Number of wins', results['states_backtrack'].count('Win'))
