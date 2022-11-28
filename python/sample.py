import argparse
import time
import random
import math

from client import Softmax


def main():
    args = parse_args()
    sample_log(args.address, args.port, args.name, args.steps)


def parse_args():
    parser = argparse.ArgumentParser()

    parser.add_argument("address")
    parser.add_argument("port", type=int)
    parser.add_argument("--name", default="Sample")
    parser.add_argument("--steps", type=int, default=50)

    args = parser.parse_args()

    return args


def sample_log(address, port, name, steps):
    softmax = Softmax(f"http://{address}:{port}", name)

    for i in range(steps):
        loss_noise = random.random() * 0.3 - 0.15
        loss = 1/(i+1)/2
        loss *= (1 + loss_noise)

        accuracy_noise = random.random() * 0.2 - 0.1
        accuracy = (1 - loss) * 100 - 20
        accuracy *= (1 + accuracy_noise)

        softmax.log(i, "loss", loss)
        softmax.log(i, "accuracy", accuracy)
        time.sleep(1)


if __name__ == "__main__":
    main()
