from pyx12.errors import EngineError, Errh_null
from pyx12.x12context import X12ContextReader
from pyx12.x12file import X12Reader
from pyx12.map_walker import walk_tree
from pyx12.params import params

def validate_edi_x12_832(file_path):
    # Initialize parameters
    param = params()
    param.set('check_for_isa', 'yes')  # Check for ISA segment
    param.set('gsvalue', '832')        # Set GS segment value for 832

    # Initialize error handler and context
    errh = Errh_null()
    context = X12ContextReader(param, errh)

    # Initialize a list to capture validation errors
    errors = []

    try:
        # Read and parse the EDI file
        with open(file_path, 'r') as edi_file:
            edi_data = edi_file.read()

        x12_reader = X12Reader(param, context, edi_data)

        # Walk through the EDI structure to validate
        for seg in walk_tree(x12_reader.get_tree()):
            if not seg.is_valid:
                # Capture invalid segments and details
                error_detail = f"Segment: {seg.get_seg_id()} at Line: {seg.get_line_number()} is invalid."
                errors.append(error_detail)

    except EngineError as e:
        errors.append(f"Engine Error: {str(e)}")
    except Exception as e:
        errors.append(f"Unexpected Error: {str(e)}")

    # Print out the validation results
    if errors:
        print("Validation failed with the following errors:")
        for error in errors:
            print(error)
    else:
        print("Validation successful! No errors found.")

# Example usage
file_path = '/mnt/data/file-6twMh4nDadVnJD8Nty1hsYXf'
validate_edi_x12_832(file_path)
