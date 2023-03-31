import React, { useState } from "react";
import styled from "styled-components";
import Survey from "./Survey";
import LogIn from "./LogIn";
import { useNavigate } from "react-router-dom";

const list = [
  {
    id: 1,
    content: "세계 여행하기",
  },
  {
    id: 2,
    content: "점심 먹기",
  },
  {
    id: 3,
    content: "점심 먹기",
  },
  {
    id: 4,
    content: "점심 먹기",
  },
  {
    id: 5,
    content: "점심 먹기",
  },
  {
    id: 6,
    content: "점심 먹기",
  },
  {
    id: 7,
    content: "점심 먹기",
  },
  {
    id: 8,
    content: "점심 먹기",
  },
];

const DivTop = styled.div`
  min-width: 350px;
  width: 100%;
  display: inline-block;
`;

const Div = styled.div`
    width: 100%;
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    &::-webkit-scrollbar {
        display: none;
    }
`

const Title = styled.h3`
  width: 100%;
  text-align: center;
  margin: 0;
`;

const NextButton = styled.button`
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  &:hover {
    box-shadow: none;
    cursor: pointer;
  }
  background: #1c9bff;
  color: #ffffff;
  border-radius: 16px;
  border: none;
  width: 40vw;
  min-width: 350px;
  height: 50px;
  font-size: 16px;
  margin-top: 20px;
`;

const brTag = <div style={{lineHeight: "500%"}}><br/></div>

export default function SurveyPage() {
    const [selected, setSelected] = useState([])
    const navigate = useNavigate();
    const changeData = (id) => {
        if (selected.includes(id)) {
            setSelected(selec => selec.filter(v => v !== id))
        } else {
            setSelected(selec => selec.concat(id))
        }
    }
    const goToSignUp = () => {
        navigate("/survey/signup", {
            state: {
                selected,
            }
        })
    }
    return (
        <DivTop>
            <LogIn/>
            {brTag}
            <Title>
                관심있는 버킷 리스트를 선택해주세요.
            </Title>
            <Title>
                {selected.length !== 0 ? selected.length : null}
            </Title>
            <Div style={{ minHeight: "400px", height: "34vw", overflow: "auto"}}>
                {list.map(data => (
                    <Survey
                    select={selected.includes(data.id)}
                    content={data.content}
                    id={data.id}
                    key={data.id}
                    propFunction={changeData}
                    />
                ))}
            </Div>
            <Div>
                <NextButton onClick={goToSignUp}>
                    다 골랐어요!
                </NextButton>
            </Div>
        </DivTop>
    )
}