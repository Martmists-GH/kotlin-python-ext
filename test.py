import pykt


def main():
    print(pykt)
    pykt.hello()

    counter = pykt.NativeCounter()
    print(counter)

    for _ in range(10):
        print(counter.increment())

    counter.value = 100
    print(counter.value)


if __name__ == "__main__":
    main()
