# BlockChain Network

In this project we simulate a multi-user network for a cryptocurrency as **Bitcoin**. Once a *transaction* is made, the user randomly picks some of his peers to notify who in turn notify others. Once a user accumulates **n** transactions, it strives to form a *block* by doing a computationally expensive task and announce it afterwards. Users who receive the announced block, add it to their version of *blockchain* (ledger) and if a majority consent is reached for the current ledger version, users of different versions throw them away and adopt the new one.

## Running the tests
1. Compile all files.
2. Run the `Main.java` file in the `src` folder.
3. Change the number of `users` and/or the created `transactions`, to see how the network responds accordingly.


## Built With

* Java :heart_eyes:
* Java SE Security

## Authors

* **Ahmed Anwar**	- [Github](https://github.com/Ahmed-anwar)
* **Esraa Salah**	- [Github](https://github.com/EsraaaSalah)
* **Hatem Morgan**	- [Github](https://github.com/HatemMorgan)
* **Hagar Mosaad**	- [Github](https://github.com/hagary)
