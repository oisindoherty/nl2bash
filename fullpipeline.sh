#!/bin/bash

git submodule update --init --recursive --depth 1

cd NL2BashWebScraper
(./gradlew run) &
PID=$!
read -p "Press enter to stop scraping. "
kill $!
cd ..

cd nl2bash_server
pip3 install pipenv
pipenv run python manage.py migrate
pipenv run python -m nl2bash_server.add_data_from_scraper ./test_pages/ScrapedPages
(pipenv run python manage.py runserver) &
PID=$!
read -p "Press enter to stop hosting the tester ui interface. "
kill $!
pipenv run python -m nl2bash_server.save_data
touch all.cm
touch all.nl
mv -f -t ../tellina/tellina_learning_module/data/bash all.cm all.nl
cd ..

cd tellina
pip3 install -r requirements.txt
make submodule
cd tellina_learning_module
pip3 install -r requirements.txt
cd experiments
# pip3 install tensorflow
# make data
# make train
make decode
make gen_auto_evaluation_table
