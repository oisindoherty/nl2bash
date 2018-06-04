import json
import os, sys
import random

def export_verify_files(data_dir, output_dir, sample_size=None):
    """Usage: export_verify_files(data_dir, output_dir, sample_size=None)
    Export all.cm and all.nl from data_dir to .verify files in output_dir.
    If sample_size is given, only take approximately that many samples
    and output the rest into a new all.cm and all.nl file pair in output_dir"""
    for split in ['all']:
        nl_file_path = os.path.join(data_dir, split + '.nl')
        cm_file_path = os.path.join(data_dir, split + '.cm')
        with open(nl_file_path) as f:
            nls = [nl.strip() for nl in f.readlines()]
        with open(cm_file_path) as f:
            cms = [cm.strip() for cm in f.readlines()]
        pop = list(enumerate(zip(nls, cms)))
        p = 1
        if (sample_size):
            p = float(sample_size) / float(len(pop))
        nl_outfile_path = os.path.join(output_dir, "all.nl")
        cm_outfile_path = os.path.join(output_dir, "all.cm")
        with open(nl_outfile_path, 'w') as nl_outfile, open(cm_outfile_path, 'w') as cm_outfile:
            for i, (nl, cm) in pop:
                if (random.random() < p):
                    outfile_path = os.path.join(output_dir, split + str(i) + '.verify')
                    with open(outfile_path, 'w') as outfile:
                        outfile.write(json.dumps({'title':nl, 'commands':[cm]}))
                else:
                    nl_outfile.write("{}\n".format(nl))
                    cm_outfile.write("{}\n".format(cm))
                
