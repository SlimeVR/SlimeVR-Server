import { useOnboarding } from '@/hooks/onboarding';
import classNames from 'classnames';
import { useState } from 'react';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { useContext } from 'react';
import { QuizContext } from '@/App';
import { Localized } from '@fluent/react';

export function UpdateQuestion() {

    const { applyProgress} = useOnboarding();
    const [outline, setOutline] = useState<'Yes' | 'No'>()
    const { setUpdate } = useContext(QuizContext);
    const [ disabled, setDisabled ] = useState(true)

    applyProgress(0.2);

        return (
            <div className="flex flex-col w-full h-full xs:justify-center items-center">

                    <div className="flex flex-col gap-2">
                        <div className="flex gap-2 items-center">
                            <Typography
                            variant="main-title"
                            id="onboarding-quiz-Update-title"
                            />
                        </div>
                        <div className=''>
                            <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
                                <Typography
                                whitespace="whitespace-pre-wrap"
                                id="onboarding-quiz-Update-description"
                                />
                            </div>
                            <div className="flex gap-2 px-2 p-6">
                                    <div
                                        onClick={() => {
                                            setOutline('Yes'); 
                                            setUpdate('Yes');
                                            setDisabled(false);
                                        }}
                                        className={classNames(
                                            'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear hover:bg-background-50 cursor-pointer bg-background-60',
                                            outline==='Yes' && 'outline outline-3 outline-accent-background-40',
                                        )}
                                    >
                                        <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border">
                                            <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
                                                <Typography id="onboarding-quiz-Update-answer-1"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div
                                        onClick={() =>  {
                                            setOutline('No'); 
                                            setUpdate('No');
                                            setDisabled(false);
                                        }}
                                        className={classNames(
                                            'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear hover:bg-background-50 cursor-pointer bg-background-60',
                                            outline==='No' && 'outline outline-3 outline-accent-background-40',
                                        )}
                                    >
                                        <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border">
                                            <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
                                                <Typography id="onboarding-quiz-Update-answer-2"/>
                                            </div>
                                        </div>
                                    </div>
                                    
                            </div>
                        </div>
                        <div className="flex px-2 p-6">
                            <Localized id="onboarding-quiz_continue">
                                <Button
                                to='/onboarding/quiz/Q3'
                                variant="primary"
                                disabled={disabled}
                                />
                            </Localized>
                        </div>
                    </div>
            </div>
        );
    }
