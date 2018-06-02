# nl2bash [![Build Status](https://travis-ci.org/oisindoherty/nl2bash.svg?branch=master)](https://travis-ci.org/oisindoherty/nl2bash)
Utilizing natural language processing to transform english requests to valid Bash commands.

This repository consists of three projects, with the combined goal of providing a utility
to translate natural language English commands into one-liners for the Bash scripting language,
including one-liners that it can piece together naturally. These three projects are:

- Tellina, a Tensorflow-based machine learning system that translates English into Bash 
  commands, using training data gathered from experts and from sites like StackOverflow.
- NL2BashWebScraper, a JSoup-based tool to gather more potential data pairs from the Internet.
- TesterUI, a django-based tool to allow users to verify these data pairs.

The Tellina project and the TesterUI are found in submodules.
To clone these submodules, use:

    $ git submodule init
    $ git submodule update

Consult each project's README.md for further build instructions for each.

Check the user manual for help using nl2bash and Tellina. The user manual can be found at
https://github.com/oisindoherty/nl2bash/blob/master/nl2bash_user_manual.pdf

# Reproducing results from start to finish
In linux, execute the following:

    $ bash fullpipeline.sh
    
Note: you will be prompted to stop the scraping/verifying (press enter for these).

The training step is very long; uncomment the line in the last section to run training.

# Reproducing results from previous state (all.cm and all.nl)
As the scraper and TesterUI both have components that are hard to reproduce (scraping and user
input), the best way to reproduce results is using the output of the TesterUI, the all.cm/.nl
files. To do this, place them in the tellina/tellina_learning_module/data/bash folder and follow the
instructions in the README of tellina/tellina_learning_module. Our latest attempt will be in
that folder, along with any other versions we've compared to.
