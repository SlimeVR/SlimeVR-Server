import { useState } from "react";
import { Button } from "../commons/Button";
import { AppModal } from "../Modal";




export function AutomaticCalibration() {

    const [isOpen, setOpen] = useState(false);
    

    return (
        <>
            <Button variant="primary" onClick={() => setOpen(true)}>Automatic calibration</Button>
            <AppModal 
                isOpen={isOpen} 
                name={<>Automatic Calibration</>} 
                onRequestClose={() => setOpen(false)}
            >
                <>
                    <div className="flex w-full justify-center">
                        <Button variant="primary">Start Calibration</Button>
                    </div>
                    <div className="flex w-full justify-between mt-5">
                        <Button variant="primary">Close</Button>
                        <Button variant="primary" disabled>Apply values</Button>
                    </div>
                </>
            </AppModal>
        </>
    )

}