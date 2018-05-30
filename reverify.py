import json
import os, sys

def export_verify_files(data_dir, output_dir):
    """Usage: export_verify_files(data_dir, output_dir)
    Export all.cm and all.nl from data_dir to .verify files in output_dir"""
    for split in ['all']:
        nl_file_path = os.path.join(data_dir, split + '.nl')
        cm_file_path = os.path.join(data_dir, split + '.cm')
        with open(nl_file_path) as f:
            nls = [nl.strip() for nl in f.readlines()]
        with open(cm_file_path) as f:
            cms = [cm.strip() for cm in f.readlines()]
        for i, (nl, cm) in enumerate(zip(nls, cms)):
            outfile_path = os.path.join(output_dir, split + str(i) + '.verify')
            with open(outfile_path, 'w') as outfile:
                outfile.write(json.dumps({'title':nl, 'commands':[cm]}))