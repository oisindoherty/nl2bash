#!/bin/bash

# Get the submodules (only depth 1 needed)
git submodule update --init --recursive

pip3 install pipenv

# Test out the web scraper
cd NL2BashWebScraper
(./gradlew run) &
PID=$!
read -p "Press enter to stop scraping. "
kill $PID
cd ..

# Test out the 
cd nl2bash_server
pipenv run python manage.py migrate
pipenv run python -m nl2bash_server.add_data_from_scraper ./test_pages/ScrapedPages
(pipenv run python manage.py runserver 0.0.0.0:12321) &
PID=$!
read -p "Press enter to stop hosting the tester ui interface. "
kill $PID
pipenv run python -m nl2bash_server.save_data
touch all.cm
touch all.nl
mv -f -t ../tellina/tellina_learning_module/data/bash all.cm all.nl
cd ..

# Test table generation (commented line below are for training, which takes hours)
cd tellina
pipenv install -r requirements.txt
pipenv run make submodule
cd tellina_learning_module
pipenv install -r requirements.txt
pipenv install tensorflow
tar -xvf nlp_tools/spellcheck/most_common.tar.xz --directory nlp_tools/spellcheck/
# pipenv run make -C ./experiments train
pipenv run make -C ./experiments data
pipenv run make -C ./experiments decode
pipenv run make -C ./experiments gen_auto_evaluation_table
