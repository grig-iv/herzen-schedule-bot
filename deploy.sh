#!/usr/bin/env sh

printf "Creating jar...\n"
lein uberjar

printf "\n\nBuilding docker image...\n"
docker build -t schedule-bot .

printf "\n\nSaving docker image...\n"
docker save -o ./target/schedule-bot.tar schedule-bot:latest

printf "\n\nCopy image to server...\n"
scp ./target/schedule-bot.tar root@37.1.221.231:/root/schedule-bot
