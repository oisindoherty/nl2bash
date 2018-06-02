import json
import os, sys
import random

def export_verify_files(data_dir, output_dir, sample_size=None):
    """Usage: export_verify_files(data_dir, output_dir)
    Export all.cm and all.nl from data_dir to .verify files in output_dir"""
    for split in ['all']:
        nl_file_path = os.path.join(data_dir, split + '.nl')
        cm_file_path = os.path.join(data_dir, split + '.cm')
        with open(nl_file_path) as f:
            nls = [nl.strip() for nl in f.readlines()]
        with open(cm_file_path) as f:
            cms = [cm.strip() for cm in f.readlines()]
        # TODO: Put everything that is not in the random sample
        # in a new .nl and .cm file pair. Specifically, you need
        # multiple samples, and a .nl/.cm file that contains
        # none of them. You might want to consider a probablistic
        # selection so that you can do this as you process the list.
        pop = enumerate(zip(nls, cms))
        if (sample_size):
            pop = random.sample(list(pop), sample_size)
        for i, (nl, cm) in pop:
            outfile_path = os.path.join(output_dir, split + str(i) + '.verify')
            with open(outfile_path, 'w') as outfile:
                outfile.write(json.dumps({'title':nl, 'commands':[cm]}))
