import React from 'react';
import { clsx } from 'clsx';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  hoverEffect?: boolean;
}

export const Card: React.FC<CardProps> = ({ children, hoverEffect = false, className, ...props }) => {
  return (
    <div
      className={clsx(
        'bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 rounded-xl p-5 shadow-sm transition-all duration-200',
        hoverEffect && 'hover:shadow-md hover:border-indigo-500/30 dark:hover:border-indigo-500/30',
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
};
